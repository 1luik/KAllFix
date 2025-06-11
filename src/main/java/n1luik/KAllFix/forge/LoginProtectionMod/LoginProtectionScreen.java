package n1luik.KAllFix.forge.LoginProtectionMod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class LoginProtectionScreen extends Screen {
    public LoginProtectionScreen() {
        super(Component.empty());
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
        //p_281549_.pose().pushPose();
        //p_281549_.pose().scale(3.3f, 3.3f, 3.3f);
        p_281549_.drawString(
                font,
                Component.translatable("k_all_fix.wait_server_send_pack",
                        I18n.get(LoginProtectionModEvent.LoginProtection ? "k_all_fix.wait_server_send_pack_wait" : "k_all_fix.wait_server_send_pack_ok")),
                20, 20, (LoginProtectionModEvent.LoginProtection ? Color.ORANGE : Color.GREEN).getRGB(),
                false);
        //p_281549_.pose().popPose();
    }
}
