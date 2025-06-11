package n1luik.K_multi_threading.debug.ex.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import me.lucko.spark.proto.SparkSamplerProtos;
import n1luik.K_multi_threading.debug.ex.DebugLog;
import n1luik.K_multi_threading.debug.ex.Relationship;
import n1luik.K_multi_threading.debug.ex.spark.SparkSave;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class KMTDebugCommand {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void register(CommandDispatcher<CommandSourceStack> p_214446_) {
        p_214446_.register(Commands.literal("#kmt_debug").requires((p_137777_) -> {
                return p_137777_.hasPermission(4);
            }).then(Commands.literal("start").then(Commands.argument("interval", DoubleArgumentType.doubleArg())
                            .executes(v->{
                                try{
                                    start((long) (DoubleArgumentType.getDouble(v, "interval") * 1000000));
                                    Relationship relationship = new Relationship();
                                    DebugLog.add(relationship);
                                    relationship.initNode(1);
                                    relationship.nodes[0].start(v.getSource().getServer().getRunningThread());
                                }catch (Throwable e){
                                    LOGGER.error("", e);
                                    throw e;
                                }
                                return 1;
                            }))
                    .executes((v) -> {
                        try{
                            start(2000000);
                            Relationship relationship = new Relationship();
                            DebugLog.add(relationship);
                            relationship.initNode(1);
                            relationship.nodes[0].start(v.getSource().getServer().getRunningThread());
                        }catch (Throwable e){
                            LOGGER.error("", e);
                            throw e;
                        }
                        return 1;
                    })
            ).then(Commands.literal("stop").then(Commands.argument("fileName", StringArgumentType.string())
                    .then(Commands.literal("spark").then(Commands.argument("spark_all", BoolArgumentType.bool())
                            .executes(v->{
                                try{
                                    return ((Command<CommandSourceStack>)KMTDebugCommand.class.getClassLoader().loadClass("n1luik.K_multi_threading.debug.ex.spark.SparkSave$Command").newInstance()).run(v);
                                }catch (Throwable e){
                                    LOGGER.error("", e);
                                    throw new RuntimeException(e);
                                }
                            })))
                    .then(Commands.literal("json")
                            .executes(v->{
                                String fileName = StringArgumentType.getString(v, "fileName");
                                Util.ioPool().execute(()->{
                                    File file = new File(fileName+".json");
                                    if (file.isDirectory()){
                                        v.getSource().sendSystemMessage(Component.literal("有一个跟文件名一样的目录|"+fileName+".json"));
                                        return;
                                    }
                                    if (file.isFile()) {
                                        v.getSource().sendSystemMessage(Component.literal("有一个同名文件，会替换掉他|"+fileName+".json"));
                                    }
                                    v.getSource().sendSystemMessage(Component.literal("正在生成"));
                                    LogRoot stop = DebugLog.stop();
                                    v.getSource().sendSystemMessage(Component.literal("正在保存"));
                                    String json = GSON.toJson(stop);
                                    v.getSource().sendSystemMessage(Component.literal("正在写入文件"));
                                    try {
                                        file.createNewFile();
                                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                                        fileOutputStream.write(json.getBytes());
                                        fileOutputStream.close();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    v.getSource().sendSystemMessage(Component.literal("写入完成"));

                                });
                                return 1;
                            }))
                    .then(Commands.literal("all")
                            .then(Commands.argument("spark_all", BoolArgumentType.bool())
                                    .executes(v->{
                                        try{
                                            return ((Command<CommandSourceStack>)KMTDebugCommand.class.getClassLoader().loadClass("n1luik.K_multi_threading.debug.ex.spark.SparkSave$CommandAll").newInstance()).run(v);
                                        }catch (Throwable e){
                                            LOGGER.error("", e);
                                            throw new RuntimeException(e);
                                        }
                                })))
            )));
    }

    public static void start(long interval) {
        DebugLog.start(interval);

    }
}
