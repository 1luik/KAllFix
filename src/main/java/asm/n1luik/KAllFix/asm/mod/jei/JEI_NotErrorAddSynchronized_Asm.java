package asm.n1luik.KAllFix.asm.mod.jei;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class JEI_NotErrorAddSynchronized_Asm implements ITransformer<ClassNode> {
    public static final List<String[]> stringsList = new ArrayList<>(List.of(
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/GuiHandlerRegistration.addGhostIngredientHandler((Ljava/lang/Class;Lmezz/jei/api/gui/handlers/IGhostIngredientHandler;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/GuiHandlerRegistration.addGuiScreenHandler((Ljava/lang/Class;Lmezz/jei/api/gui/handlers/IScreenHandler;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/GuiHandlerRegistration.addGlobalGuiHandler(Lmezz/jei/api/gui/handlers/IGuiContainerHandler;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/GuiHandlerRegistration.addGenericGuiContainerHandler(Ljava/lang/Class;Lmezz/jei/api/gui/handlers/IGuiContainerHandler;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/GuiHandlerRegistration.addGuiContainerHandler(Ljava/lang/Class;Lmezz/jei/api/gui/handlers/IGuiContainerHandler;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/gui/GuiContainerHandlers.add(Ljava/lang/Class;Lmezz/jei/api/gui/handlers/IGuiContainerHandler;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/core/collect/Table.computeIfAbsent(Ljava/lang/Object;Ljava/lang/Object;Ljava/util/function/Supplier;)Ljava/lang/Object;"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/core/collect/Table.put(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/core/collect/Table.getRow(Ljava/lang/Object;)Ljava/util/Map;"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/core/collect/Table.clear()V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/plugins/vanilla/brewing/BrewingRecipeUtil.addRecipe(Ljava/util/List;Lnet/minecraft/world/item/ItemStack;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/recipes/collect/RecipeIngredientTable.add(Ljava/lang/Object;Lmezz/jei/api/recipe/RecipeType;Ljava/util/Collection;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/forge/platform/ModHelper.getModNameForModId(Ljava/lang/String;)Ljava/lang/String;"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/RecipeCatalystRegistration.addRecipeCatalyst(Lmezz/jei/api/ingredients/IIngredientType;Ljava/lang/Object;[Lmezz/jei/api/recipe/RecipeType;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/RecipeCatalystRegistration.addRecipeCatalysts(Lmezz/jei/api/recipe/RecipeType;Lnet/minecraft/world/level/ItemLike;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/RecipeCatalystRegistration.addRecipeCatalysts(Lmezz/jei/api/recipe/RecipeType;Lmezz/jei/api/ingredients/IIngredientType;Ljava/util/List;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/load/registration/RecipeCatalystRegistration.getRecipeCatalysts()Lcom/google/common/collect/ImmutableListMultimap;"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/recipes/collect/RecipeMap.addCatalystForCategory(Lmezz/jei/api/recipe/RecipeType;Lmezz/jei/api/ingredients/ITypedIngredient;)V"),
            ForgeAsm.minecraft_map.mapMethod("mezz/jei/library/recipes/collect/RecipeMap.addRecipe(Lmezz/jei/api/recipe/RecipeType;Ljava/lang/Object;Lmezz/jei/library/ingredients/IIngredientSupplier;)V")
    ));

    int posfilter = Opcodes.ACC_PUBLIC;
    int negfilter = /*Opcodes.ACC_STATIC |*/ Opcodes.ACC_SYNTHETIC/* | Opcodes.ACC_NATIVE */| Opcodes.ACC_ABSTRACT
            /*| Opcodes.ACC_ABSTRACT*/ | Opcodes.ACC_BRIDGE;

    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {

        for (String[] strings : stringsList) {
            if (input.name.equals(strings[0])){
                boolean debug_add1 = false;
                for (MethodNode method : input.methods) {
                    if ((method.name.equals(strings[1]) && method.desc.equals(strings[2]))) {
                        debug_add1 = true;
                        if ((input.access & Opcodes.ACC_STATIC) == 0) {
                            if ((input.access & Opcodes.ACC_INTERFACE) == 0) {
                                log.info("add {} synchronized", Arrays.toString(strings));

                                if (!input.name.contains("$")) {
                                    method.access |= Opcodes.ACC_SYNCHRONIZED;
                                } else {
                                    String parent = null;
                                    String map = null;
                                    for (FieldNode fn : input.fields) {
                                        if (fn.name.equals("this$0") || ForgeAsm.srg$Forge$_map.mapField(input.name + "." + fn.name)[1].equals("this$0")) {

                                            map = fn.name;
                                            parent = fn.desc;
                                        }
                                    }
                                    if (parent == null || map == null) {
                                        method.access |= Opcodes.ACC_SYNCHRONIZED;
                                        log.error("Inner class faliure; parent not found " + (parent == null ? "null" : parent) + " " + (map == null ? "null" : map) + " " + Arrays.toString(strings));
                                        return input;
                                    }
                                    InsnList start;
                                    InsnList end;
                                    if ((method.access & negfilter) == 0 && !method.name.equals("<init>")) {
                                        start = new InsnList();
                                        start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        start.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                        start.add(new InsnNode(Opcodes.MONITORENTER));
                                        end = new InsnList();
                                        end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                        end.add(new InsnNode(Opcodes.MONITOREXIT));
                                        InsnList il = method.instructions;
                                        AbstractInsnNode ain = il.getFirst();
                                        while (ain != null) {
                                            if (ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                                                    || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                                                    || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                                                il.insertBefore(ain, end);
                                                end = new InsnList();
                                                end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                                end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                                end.add(new InsnNode(Opcodes.MONITOREXIT));
                                            }
                                            ain = ain.getNext();
                                        }
                                        il.insertBefore(il.getFirst(), start);
                                    }
                                    log.info("sync_fu " + input.name + " InnerClass Transformer Complete");
                                }
                            } else {
                                log.info("add {} synchronized", Arrays.toString(strings));
                                InsnList start;
                                InsnList end;
                                if ((method.access & negfilter) == 0 && !method.name.equals("<init>")) {
                                    start = new InsnList();
                                    start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    start.add(new InsnNode(Opcodes.MONITORENTER));
                                    end = new InsnList();
                                    end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    end.add(new InsnNode(Opcodes.MONITOREXIT));
                                    InsnList il = method.instructions;
                                    AbstractInsnNode ain = il.getFirst();
                                    while (ain != null) {
                                        if (ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                                                || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                                                || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                                            il.insertBefore(ain, end);
                                            end = new InsnList();
                                            end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                            end.add(new InsnNode(Opcodes.MONITOREXIT));
                                        }
                                        ain = ain.getNext();
                                    }
                                    il.insertBefore(il.getFirst(), start);
                                }
                            }
                        } else {
                            input.access &= Opcodes.ACC_SYNTHETIC;
                        }
                    }
                }

                if (!debug_add1){
                    log.warn("Not mapping error: {}" , Arrays.toString(strings));
                }
            }
        }


        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {

        File f = new File("config/jei-sync-ModMethod-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals("")))
                        .map(ForgeAsm.minecraft_map::mapMethod).forEach(stringsList::add);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write("""
                        // 使用//或#屏蔽
                        // 这个文件是用于对单独的函数添加synchronized
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache.removeEntity(Lnet/minecraft/world/entity/Entity;)V
                        //跟映射表格式一样simple
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> list = new ArrayList<>();
        for (String[] strings : stringsList) {
            if (!list.contains(strings[0])) {
                list.add(strings[0]);
            }
        }

        return Set.of(list.stream().map(Target::targetClass).toArray(Target[]::new));
    }
}
