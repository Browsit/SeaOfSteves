package org.browsit.seaofsteves.settings;

import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.util.IO;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GearSettings {
    private final SeaOfSteves plugin;
    private boolean sabreEnabled;
    private String sabreDisplay;
    private List<String> sabreLore;
    private int sabreSlot;
    private String sabreMythic;
    private boolean longbowEnabled;
    private String longbowDisplay;
    private List<String> longbowLore;
    private int longbowSlot;
    private int longbowSlotArrow;
    private String longbowMythic;
    private boolean scoopEnabled;
    private String scoopDisplay;
    private List<String> scoopLore;
    private int scoopSlot;
    private String scoopMythic;
    private boolean diviningRodEnabled;
    private String diviningRodDisplay;
    private List<String> diviningRodLore;
    private int diviningRodSlot;
    private String diviningMythic;
    private boolean dingyEnabled;
    private String dingyDisplay;
    private List<String> dingyLore;
    private int dingySlot;
    private String dingyMythic;
    private boolean spyglassEnabled;
    private String spyglassDisplay;
    private List<String> spyglassLore;
    private int spyglassSlot;
    private String spyglassMythic;
    private boolean fireballEnabled;
    private String fireballDisplay;
    private List<String> fireballLore;
    private int fireballSlot;
    private String fireballMythic;
    private boolean surveyorEnabled;
    private String surveyorDisplay;
    private List<String> surveyorLore;
    private int surveyorSlot;
    private String surveyorMythic;
    private boolean fishingRodEnabled;
    private String fishingRodDisplay;
    private List<String> fishingRodLore;
    private int fishingRodSlot;
    private String fishingRodMythic;
    private boolean pickaxeEnabled;
    private String pickaxeDisplay;
    private List<String> pickaxeLore;
    private int pickaxeSlot;
    private String pickaxeMythic;
    private LinkedList<String> navalSlotsMythic = new LinkedList<>();

    public GearSettings(SeaOfSteves plugin) {
        this.plugin = plugin;
    }

    public boolean isSabreEnabled() {
        return sabreEnabled;
    }

    public void setSabreEnabled(final boolean sabreEnabled) {
        this.sabreEnabled = sabreEnabled;
    }

    public String getSabreDisplay() {
        return sabreDisplay;
    }

    public void setSabreDisplay(final String sabreDisplay) {
        this.sabreDisplay = sabreDisplay;
    }

    public List<String> getSabreLore() {
        return sabreLore;
    }

    public void setSabreLore(final List<String> sabreLore) {
        this.sabreLore = sabreLore;
    }

    public int getSabreSlot() {
        return sabreSlot;
    }

    public void setSabreSlot(final int sabreSlot) {
        this.sabreSlot = sabreSlot;
    }

    public String getSabreMythic() {
        return sabreMythic;
    }

    public void setSabreMythic(final String sabreMythic) {
        this.sabreMythic = sabreMythic;
    }

    public boolean isLongbowEnabled() {
        return longbowEnabled;
    }

    public void setLongbowEnabled(final boolean longbowEnabled) {
        this.longbowEnabled = longbowEnabled;
    }

    public String getLongbowDisplay() {
        return longbowDisplay;
    }

    public void setLongbowDisplay(final String longbowDisplay) {
        this.longbowDisplay = longbowDisplay;
    }

    public List<String> getLongbowLore() {
        return longbowLore;
    }

    public void setLongbowLore(final List<String> longbowLore) {
        this.longbowLore = longbowLore;
    }

    public int getLongbowSlot() {
        return longbowSlot;
    }

    public void setLongbowSlot(final int longbowSlot) {
        this.longbowSlot = longbowSlot;
    }

    public int getLongbowSlotArrow() {
        return longbowSlotArrow;
    }

    public void setLongbowSlotArrow(final int longbowSlotArrow) {
        this.longbowSlotArrow = longbowSlotArrow;
    }

    public String getLongbowMythic() {
        return longbowMythic;
    }

    public void setLongbowMythic(final String longbowMythic) {
        this.longbowMythic = longbowMythic;
    }

    public boolean isScoopEnabled() {
        return scoopEnabled;
    }

    public void setScoopEnabled(final boolean scoopEnabled) {
        this.scoopEnabled = scoopEnabled;
    }

    public String getScoopDisplay() {
        return scoopDisplay;
    }

    public void setScoopDisplay(final String scoopDisplay) {
        this.scoopDisplay = scoopDisplay;
    }

    public List<String> getScoopLore() {
        return scoopLore;
    }

    public void setScoopLore(final List<String> scoopLore) {
        this.scoopLore = scoopLore;
    }

    public int getScoopSlot() {
        return scoopSlot;
    }

    public void setScoopSlot(final int scoopSlot) {
        this.scoopSlot = scoopSlot;
    }

    public String getScoopMythic() {
        return scoopMythic;
    }

    public void setScoopMythic(final String scoopMythic) {
        this.scoopMythic = scoopMythic;
    }

    public boolean isDiviningRodEnabled() {
        return diviningRodEnabled;
    }

    public void setDiviningRodEnabled(final boolean diviningRodEnabled) {
        this.diviningRodEnabled = diviningRodEnabled;
    }

    public String getDiviningRodDisplay() {
        return diviningRodDisplay;
    }

    public void setDiviningRodDisplay(final String diviningRodDisplay) {
        this.diviningRodDisplay = diviningRodDisplay;
    }

    public List<String> getDiviningRodLore() {
        return diviningRodLore;
    }

    public void setDiviningRodLore(final List<String> diviningRodLore) {
        this.diviningRodLore = diviningRodLore;
    }

    public int getDiviningRodSlot() {
        return diviningRodSlot;
    }

    public void setDiviningRodSlot(final int diviningRodSlot) {
        this.diviningRodSlot = diviningRodSlot;
    }

    public String getDiviningMythic() {
        return diviningMythic;
    }

    public void setDiviningMythic(final String diviningMythic) {
        this.diviningMythic = diviningMythic;
    }

    public boolean isDingyEnabled() {
        return dingyEnabled;
    }

    public void setDingyEnabled(final boolean dingyEnabled) {
        this.dingyEnabled = dingyEnabled;
    }

    public String getDingyDisplay() {
        return dingyDisplay;
    }

    public void setDingyDisplay(final String dingyDisplay) {
        this.dingyDisplay = dingyDisplay;
    }

    public List<String> getDingyLore() {
        return dingyLore;
    }

    public void setDingyLore(final List<String> dingyLore) {
        this.dingyLore = dingyLore;
    }

    public int getDingySlot() {
        return dingySlot;
    }

    public void setDingySlot(final int dingySlot) {
        this.dingySlot = dingySlot;
    }

    public String getDingyMythic() {
        return dingyMythic;
    }

    public void setDingyMythic(final String dingyMythic) {
        this.dingyMythic = dingyMythic;
    }

    public boolean isFireballEnabled() {
        return fireballEnabled;
    }

    public void setFireballEnabled(final boolean fireballEnabled) {
        this.fireballEnabled = fireballEnabled;
    }

    public String getFireballDisplay() {
        return fireballDisplay;
    }

    public void setFireballDisplay(final String fireballDisplay) {
        this.fireballDisplay = fireballDisplay;
    }

    public List<String> getFireballLore() {
        return fireballLore;
    }

    public void setFireballLore(final List<String> fireballLore) {
        this.fireballLore = fireballLore;
    }

    public int getFireballSlot() {
        return fireballSlot;
    }

    public void setFireballSlot(final int fireballSlot) {
        this.fireballSlot = fireballSlot;
    }

    public String getFireballMythic() {
        return fireballMythic;
    }

    public void setFireballMythic(final String fireballMythic) {
        this.fireballMythic = fireballMythic;
    }

    public boolean isSpyglassEnabled() {
        return spyglassEnabled;
    }

    public void setSpyglassEnabled(final boolean spyglassEnabled) {
        this.spyglassEnabled = spyglassEnabled;
    }

    public String getSpyglassDisplay() {
        return spyglassDisplay;
    }

    public void setSpyglassDisplay(final String spyglassDisplay) {
        this.spyglassDisplay = spyglassDisplay;
    }

    public List<String> getSpyglassLore() {
        return spyglassLore;
    }

    public void setSpyglassLore(final List<String> spyglassLore) {
        this.spyglassLore = spyglassLore;
    }

    public int getSpyglassSlot() {
        return spyglassSlot;
    }

    public void setSpyglassSlot(final int spyglassSlot) {
        this.spyglassSlot = spyglassSlot;
    }

    public String getSpyglassMythic() {
        return spyglassMythic;
    }

    public void setSpyglassMythic(final String spyglassMythic) {
        this.spyglassMythic = spyglassMythic;
    }

    public boolean isSurveyorEnabled() {
        return surveyorEnabled;
    }

    public void setSurveyorEnabled(final boolean surveyorEnabled) {
        this.surveyorEnabled = surveyorEnabled;
    }

    public String getSurveyorDisplay() {
        return surveyorDisplay;
    }

    public void setSurveyorDisplay(final String surveyorDisplay) {
        this.surveyorDisplay = surveyorDisplay;
    }

    public List<String> getSurveyorLore() {
        return surveyorLore;
    }

    public void setSurveyorLore(final List<String> surveyorLore) {
        this.surveyorLore = surveyorLore;
    }

    public int getSurveyorSlot() {
        return surveyorSlot;
    }

    public void setSurveyorSlot(final int surveyorSlot) {
        this.surveyorSlot = surveyorSlot;
    }

    public String getSurveyorMythic() {
        return surveyorMythic;
    }

    public void setSurveyorMythic(final String surveyorMythic) {
        this.surveyorMythic = surveyorMythic;
    }

    public boolean isFishingRodEnabled() {
        return fishingRodEnabled;
    }

    public void setFishingRodEnabled(final boolean fishingRodEnabled) {
        this.fishingRodEnabled = fishingRodEnabled;
    }

    public String getFishingRodDisplay() {
        return fishingRodDisplay;
    }

    public void setFishingRodDisplay(final String fishingRodDisplay) {
        this.fishingRodDisplay = fishingRodDisplay;
    }

    public List<String> getFishingRodLore() {
        return fishingRodLore;
    }

    public void setFishingRodLore(final List<String> fishingRodLore) {
        this.fishingRodLore = fishingRodLore;
    }

    public int getFishingRodSlot() {
        return fishingRodSlot;
    }

    public void setFishingRodSlot(final int fishingRodSlot) {
        this.fishingRodSlot = fishingRodSlot;
    }

    public String getFishingRodMythic() {
        return fishingRodMythic;
    }

    public void setFishingRodMythic(final String fishingRodMythic) {
        this.fishingRodMythic = fishingRodMythic;
    }

    public boolean isPickaxeEnabled() {
        return pickaxeEnabled;
    }

    public void setPickaxeEnabled(final boolean pickaxeEnabled) {
        this.pickaxeEnabled = pickaxeEnabled;
    }

    public String getPickaxeDisplay() {
        return pickaxeDisplay;
    }

    public void setPickaxeDisplay(final String pickaxeDisplay) {
        this.pickaxeDisplay = pickaxeDisplay;
    }

    public List<String> getPickaxeLore() {
        return pickaxeLore;
    }

    public void setPickaxeLore(final List<String> pickaxeLore) {
        this.pickaxeLore = pickaxeLore;
    }

    public int getPickaxeSlot() {
        return pickaxeSlot;
    }

    public void setPickaxeSlot(final int pickaxeSlot) {
        this.pickaxeSlot = pickaxeSlot;
    }

    public String getPickaxeMythic() {
        return pickaxeMythic;
    }

    public void setPickaxeMythic(final String pickaxeMythic) {
        this.pickaxeMythic = pickaxeMythic;
    }

    public LinkedList<String> getNavalSlotsMythic() {
        return navalSlotsMythic;
    }

    public void setNavalSlotsMythic(final LinkedList<String> navalSlotsMythic) {
        this.navalSlotsMythic = navalSlotsMythic;
    }

    public void load() {
        final FileConfiguration cfg = IO.getItems();
        sabreEnabled = cfg.getBoolean("sos.gear.sabre.enabled", true);
        sabreDisplay = translate(cfg.getString("sos.gear.sabre.display", "&6Sabre"));
        sabreLore = translateList(cfg.getStringList("sos.gear.sabre.lore"));
        sabreSlot = cfg.getInt("sos.gear.sabre.slot", 0);
        sabreMythic = cfg.getString("sos.gear.sabre.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        longbowEnabled = cfg.getBoolean("sos.gear.longbow.enabled", true);
        longbowDisplay = translate(cfg.getString("sos.gear.longbow.display", "&6Longbow"));
        longbowLore = translateList(cfg.getStringList("sos.gear.longbow.lore"));
        longbowSlot = cfg.getInt("sos.gear.longbow.slot", 1);
        longbowSlotArrow = cfg.getInt("sos.gear.longbow.slot-arrow", -1);
        longbowMythic = cfg.getString("sos.gear.longbow.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        scoopEnabled = cfg.getBoolean("sos.gear.scoop.enabled", true);
        scoopDisplay = translate(cfg.getString("sos.gear.scoop.display", "&6Scoop"));
        scoopLore = translateList(cfg.getStringList("sos.gear.scoop.lore"));
        scoopSlot = cfg.getInt("sos.gear.scoop.slot", 2);
        scoopMythic = cfg.getString("sos.gear.scoop.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        diviningRodEnabled = cfg.getBoolean("sos.gear.divining-rod.enabled", true);
        diviningRodDisplay = translate(cfg.getString("sos.gear.divining-rod.display", "&6Divining Rod"));
        diviningRodLore = translateList(cfg.getStringList("sos.gear.divining-rod.lore"));
        diviningRodSlot = cfg.getInt("sos.gear.divining-rod.slot", 3);
        diviningMythic = cfg.getString("sos.gear.divining-rod.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        dingyEnabled = cfg.getBoolean("sos.gear.ship.enabled", true);
        dingyDisplay = translate(cfg.getString("sos.gear.ship.display", "&6Ship"));
        dingyLore = translateList(cfg.getStringList("sos.gear.ship.lore"));
        dingySlot = cfg.getInt("sos.gear.ship.slot", 4);
        dingyMythic = cfg.getString("sos.gear.ship.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        fireballEnabled = cfg.getBoolean("sos.gear.fireball.enabled", true);
        fireballDisplay = translate(cfg.getString("sos.gear.fireball.display", "&6Fire Charge"));
        fireballLore = translateList(cfg.getStringList("sos.gear.fireball.lore"));
        fireballSlot = cfg.getInt("sos.gear.fireball.slot", 5);
        fireballMythic = cfg.getString("sos.gear.fireball.mythic", "VOTSAmmoCannonBall");
        spyglassEnabled = cfg.getBoolean("sos.gear.spyglass.enabled", true);
        spyglassDisplay = translate(cfg.getString("sos.gear.spyglass.display", "&6Spyglass"));
        spyglassLore = translateList(cfg.getStringList("sos.gear.spyglass.lore"));
        spyglassSlot = cfg.getInt("sos.gear.spyglass.slot", 6);
        spyglassMythic = cfg.getString("sos.gear.spyglass.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        surveyorEnabled = cfg.getBoolean("sos.gear.surveyor.enabled", true);
        surveyorDisplay = translate(cfg.getString("sos.gear.surveyor.display", "&6Surveyor"));
        surveyorLore = translateList(cfg.getStringList("sos.gear.surveyor.lore"));
        surveyorSlot = cfg.getInt("sos.gear.surveyor.slot", 7);
        surveyorMythic = cfg.getString("sos.gear.surveyor.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        fishingRodEnabled = cfg.getBoolean("sos.gear.fishing-rod.enabled", true);
        fishingRodDisplay = translate(cfg.getString("sos.gear.fishing-rod.display", "&6Fishing Rod"));
        fishingRodLore = translateList(cfg.getStringList("sos.gear.fishing-rod.lore"));
        fishingRodSlot = cfg.getInt("sos.gear.fishing-rod.slot", 8);
        fishingRodMythic = cfg.getString("sos.gear.fishing-rod.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        pickaxeEnabled = cfg.getBoolean("sos.gear.pickaxe.enabled", false);
        pickaxeDisplay = translate(cfg.getString("sos.gear.pickaxe.display", "&6Pickaxe"));
        pickaxeLore = translateList(cfg.getStringList("sos.gear.pickaxe.lore"));
        pickaxeSlot = cfg.getInt("sos.gear.pickaxe.slot", 5);
        pickaxeMythic = cfg.getString("sos.gear.pickaxe.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD");
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-0.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD"));
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-1.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD"));
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-2.mythic", "VOTSMediumShipRepairWeak"));
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-3.mythic", "VOTSDirectionControler"));
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-4.mythic", "VOTSSpecialControler"));
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-5.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD"));
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-6.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD"));
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-7.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD"));
        navalSlotsMythic.add(cfg.getString("sos.gear-naval.slot-8.mythic", "NAME_OF_ITEM_TO_USE_INSTEAD"));
    }

    private String translate(final String str) {
        return ChatColor.translateAlternateColorCodes('&', str.strip());
    }
    
    private List<String> translateList(List<String> list) {
        final List<String> fin = new ArrayList<>();
        for (final String str : list) {
            fin.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return fin;
    }
}
