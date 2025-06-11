package n1luik.KAllFix.fix.petrolpark;

import com.petrolpark.shop.ShopMenuItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import static com.petrolpark.Petrolpark.REGISTRATE;
@Deprecated
public class MockPetrolparkItems {


    public static ItemEntry<ShopMenuItem> register() {
        return REGISTRATE.item("menu", ShopMenuItem::new).register();
    }
}
