package n1luik.K_multi_threading.fix;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import n1luik.K_multi_threading.core.Imixin.IFixGetterRoot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class FixGetterRoot<T> implements IFixGetterRoot {
    private static int size = 0;
    public static List<FixGetterRoot<?>> roots = new CopyOnWriteArrayList<>();
    public static synchronized <T> FixGetterRoot<T> create(String name,
                                                           BooleanSupplier isEnabled,
                                                           Supplier<Function<ServerLevel, @Nullable T>> worldNew,
                                                           Supplier<Function<ServerPlayer, @Nullable T>> playerNew,
                                                           Supplier<Function<MinecraftServer, @Nullable T>> serverNew) {
        if (isEnabled.getAsBoolean()) {
            return new FixGetterRoot<>(name, true, size++, worldNew.get(), playerNew.get(), serverNew.get());

        }else {
            return new FixGetterRoot<>(name, false, -1, v->null, v->null, v->null);
        }

    }
    static {
        for (ICreateFix iCreateFix : ServiceLoader.load(ICreateFix.class, FixGetterRoot.class.getClassLoader())) {
            FixGetterRoot<?> fix = iCreateFix.createFix();
            if (fix.enable) {
                roots.add(fix);
                log.info("[FixGetter] Loaded: {}", fix.name);
            }
        }
    }
    public static void cinit(){};

    public final String name;
    public final boolean enable;
    public final int id;
    public final Function<ServerLevel, @Nullable T> worldNew;
    public final Function<ServerPlayer, @Nullable T> playerNew;
    public final Function<MinecraftServer, @Nullable T> serverNew;


    public FixGetterRoot(String name, boolean enable, int id,
                         Function<ServerLevel, @Nullable T> worldNew,
                         Function<ServerPlayer, @Nullable T> playerNew,
                         Function<MinecraftServer, @Nullable T> serverNew
    ) {
        this.name = name;
        this.enable = enable;
        this.id = id;
        this.worldNew = worldNew;
        this.playerNew = playerNew;
        this.serverNew = serverNew;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }
}
