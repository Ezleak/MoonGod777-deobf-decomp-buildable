package me.moon.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.GraphicsEnvironment;
import me.moon.Moon;
import me.moon.event.events.ClientEvent;
import me.moon.features.command.Command;
import me.moon.features.modules.Module;
import me.moon.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FontMod extends Module {
   private static FontMod INSTANCE = new FontMod();
   public Setting<String> fontName = this.register(new Setting<>("FontName", "Arial", "Name of the font."));
   public Setting<Boolean> antiAlias = this.register(new Setting<>("AntiAlias", true, "Smoother font."));
   public Setting<Boolean> fractionalMetrics = this.register(new Setting<>("Metrics", true, "Thinner font."));
   public Setting<Integer> fontSize = this.register(new Setting<>("Size", 18, 12, 30, "Size of the font."));
   public Setting<Integer> fontStyle = this.register(new Setting<>("Style", 0, 0, 3, "Style of the font."));
   private boolean reloadFont = false;

   public FontMod() {
      super("CustomFont", "CustomFont for all of the clients text. Use the font command.", Module.Category.CLIENT, true, false, false);
      this.setInstance();
   }

   public static FontMod getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new FontMod();
      }

      return INSTANCE;
   }

   public static boolean checkFont(String font, boolean message) {
      for(String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
         if (!message && s.equals(font)) {
            return true;
         }

         if (message) {
            Command.sendMessage(s);
         }
      }

      return false;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      Setting setting;
      if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this)) {
         if (setting.getName().equals("FontName") && !checkFont(setting.getPlannedValue().toString(), false)) {
            Command.sendMessage(ChatFormatting.RED + "That font doesnt exist.");
            event.setCanceled(true);
            return;
         }

         this.reloadFont = true;
      }
   }

   @Override
   public void onTick() {
      if (this.reloadFont) {
         Moon.textManager.init(false);
         this.reloadFont = false;
      }
   }
}
