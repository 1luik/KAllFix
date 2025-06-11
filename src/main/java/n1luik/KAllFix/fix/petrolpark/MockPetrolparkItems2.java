package n1luik.KAllFix.fix.petrolpark;

import com.petrolpark.shop.ShopMenuItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import java.lang.reflect.InvocationTargetException;

import static com.petrolpark.Petrolpark.REGISTRATE;

/**
 * 没事的就拉一点屎，我不想多试一次
 * */
@Deprecated
public class MockPetrolparkItems2 {


    public static ItemEntry<ShopMenuItem> register() {
        try {
            return (ItemEntry<ShopMenuItem>)MockPetrolparkItems2.class.getClassLoader()
                    .loadClass("n1luik.KAllFix.fix.petrolpark.MockPetrolparkItems")
                    .getMethod("register")
                    .invoke(null);
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
