package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*--  NewRandomizerGUI.java - the main GUI for the randomizer, containing   --*/
/*--                          the various options available and such        --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import com.dabomstew.pkrandom.*;
import com.dabomstew.pkrandom.Script.*;
import com.dabomstew.pkrandom.Script.JythonTokenMaker;
import com.dabomstew.pkrandom.cli.CliRandomizer;
import com.dabomstew.pkrandom.constants.GlobalConstants;
import com.dabomstew.pkrandom.exceptions.EncryptedROMException;
import com.dabomstew.pkrandom.exceptions.InvalidSupplementFilesException;
import com.dabomstew.pkrandom.exceptions.RandomizationException;
import com.dabomstew.pkrandom.pokemon.ExpCurve;
import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.*;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.python.google.common.primitives.Ints;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NewRandomizerGUI {
    private JTabbedPane tabbedPane1;
    private JCheckBox raceModeCheckBox;
    private JButton openROMButton;
    private JButton randomizeSaveButton;
    private JButton premadeSeedButton;
    private JButton settingsButton;
    private JButton loadSettingsButton;
    private JButton saveSettingsButton;
    private JPanel mainPanel;
    private JRadioButton pbsUnchangedRadioButton;
    private JRadioButton pbsShuffleRadioButton;
    private JRadioButton pbsRandomRadioButton;
    private JRadioButton pbsLegendariesSlowRadioButton;
    private JRadioButton pbsStrongLegendariesSlowRadioButton;
    private JRadioButton pbsAllMediumFastRadioButton;
    private JRadioButton pbsStandardizeEXPCurvesRadioButton;
    private JCheckBox pbsFollowEvolutionsCheckBox;
    private JCheckBox pbsUpdateBaseStatsCheckBox;
    private JCheckBox ptIsDualTypeCheckBox;
    private JRadioButton ptUnchangedRadioButton;
    private JRadioButton ptRandomFollowEvolutionsRadioButton;
    private JRadioButton ptRandomCompletelyRadioButton;
    private JRadioButton paUnchangedRadioButton;
    private JRadioButton paRandomRadioButton;
    private JCheckBox paAllowWonderGuardCheckBox;
    private JCheckBox paFollowEvolutionsCheckBox;
    private JCheckBox paTrappingAbilitiesCheckBox;
    private JCheckBox paNegativeAbilitiesCheckBox;
    private JCheckBox paBadAbilitiesCheckBox;
    private JRadioButton peUnchangedRadioButton;
    private JRadioButton peRandomRadioButton;
    private JCheckBox peSimilarStrengthCheckBox;
    private JCheckBox peSameTypingCheckBox;
    private JCheckBox peLimitEvolutionsToThreeCheckBox;
    private JCheckBox peForceChangeCheckBox;
    private JCheckBox peChangeImpossibleEvosCheckBox;
    private JCheckBox peMakeEvolutionsEasierCheckBox;
    private JRadioButton spUnchangedRadioButton;
    private JRadioButton spCustomRadioButton;
    private JRadioButton spRandomCompletelyRadioButton;
    private JRadioButton spRandomTwoEvosRadioButton;

    private JRadioButton spScriptedRadioButton;

    private JComboBox<String> spComboBox1;
    private JComboBox<String> spComboBox2;
    private JComboBox<String> spComboBox3;
    private JRadioButton spRandomizeStarterHeldItemsRadioButton;
    private JCheckBox spBanBadItemsCheckBox;
    private JRadioButton stpUnchangedRadioButton;
    private JRadioButton stpSwapLegendariesSwapStandardsRadioButton;
    private JRadioButton stpRandomCompletelyRadioButton;
    private JRadioButton stpRandomSimilarStrengthRadioButton;
    private JCheckBox stpLimitMainGameLegendariesCheckBox;
    private JCheckBox stpRandomize600BSTCheckBox;
    private JRadioButton igtUnchangedRadioButton;
    private JRadioButton igtRandomizeGivenPokemonOnlyRadioButton;
    private JRadioButton igtRandomizeBothRequestedGivenRadioButton;
    private JCheckBox igtRandomizeNicknamesCheckBox;
    private JCheckBox igtRandomizeOTsCheckBox;
    private JCheckBox igtRandomizeIVsCheckBox;
    private JCheckBox igtRandomizeItemsCheckBox;
    private JCheckBox mdRandomizeMovePowerCheckBox;
    private JCheckBox mdRandomizeMoveAccuracyCheckBox;
    private JCheckBox mdRandomizeMovePPCheckBox;
    private JCheckBox mdRandomizeMoveTypesCheckBox;
    private JCheckBox mdRandomizeMoveCategoryCheckBox;
    private JCheckBox mdUpdateMovesCheckBox;
    private JCheckBox mdLegacyCheckBox;
    private JRadioButton pmsUnchangedRadioButton;
    private JRadioButton pmsRandomPreferringSameTypeRadioButton;
    private JRadioButton pmsRandomCompletelyRadioButton;
    private JRadioButton pmsMetronomeOnlyModeRadioButton;
    private JCheckBox pmsGuaranteedLevel1MovesCheckBox;
    private JCheckBox pmsReorderDamagingMovesCheckBox;
    private JCheckBox pmsNoGameBreakingMovesCheckBox;
    private JCheckBox pmsForceGoodDamagingCheckBox;
    private JSlider pmsGuaranteedLevel1MovesSlider;
    private JSlider pmsForceGoodDamagingSlider;
    private JCheckBox tpRivalCarriesStarterCheckBox;
    private JCheckBox tpSimilarStrengthCheckBox;
    private JCheckBox tpWeightTypesCheckBox;
    private JCheckBox tpDontUseLegendariesCheckBox;
    private JCheckBox tpNoEarlyWonderGuardCheckBox;
    private JCheckBox tpRandomizeTrainerNamesCheckBox;
    private JCheckBox tpRandomizeTrainerClassNamesCheckBox;
    private JCheckBox tpForceFullyEvolvedAtCheckBox;
    private JSlider tpForceFullyEvolvedAtSlider;
    private JSlider tpPercentageLevelModifierSlider;
    private JCheckBox tpEliteFourUniquePokemonCheckBox;
    private JSpinner tpEliteFourUniquePokemonSpinner;
    private JCheckBox tpPercentageLevelModifierCheckBox;
    private JRadioButton wpUnchangedRadioButton;
    private JRadioButton wpRandomRadioButton;
    private JRadioButton wpArea1To1RadioButton;
    private JRadioButton wpGlobal1To1RadioButton;
    private JRadioButton wpARNoneRadioButton;
    private JRadioButton wpARSimilarStrengthRadioButton;
    private JRadioButton wpARCatchEmAllModeRadioButton;
    private JRadioButton wpARTypeThemeAreasRadioButton;
    private JCheckBox wpUseTimeBasedEncountersCheckBox;
    private JCheckBox wpDontUseLegendariesCheckBox;
    private JCheckBox wpSetMinimumCatchRateCheckBox;
    private JCheckBox wpRandomizeHeldItemsCheckBox;
    private JCheckBox wpBanBadItemsCheckBox;
    private JCheckBox wpBalanceShakingGrassPokemonCheckBox;
    private JCheckBox wpPercentageLevelModifierCheckBox;
    private JSlider wpPercentageLevelModifierSlider;
    private JSlider wpSetMinimumCatchRateSlider;
    private JRadioButton tmUnchangedRadioButton;
    private JRadioButton tmRandomRadioButton;
    private JCheckBox tmFullHMCompatibilityCheckBox;
    private JCheckBox tmLevelupMoveSanityCheckBox;
    private JCheckBox tmKeepFieldMoveTMsCheckBox;
    private JCheckBox tmForceGoodDamagingCheckBox;
    private JSlider tmForceGoodDamagingSlider;
    private JRadioButton thcUnchangedRadioButton;
    private JRadioButton thcRandomPreferSameTypeRadioButton;
    private JRadioButton thcRandomCompletelyRadioButton;
    private JRadioButton thcFullCompatibilityRadioButton;
    private JRadioButton mtUnchangedRadioButton;
    private JRadioButton mtRandomRadioButton;
    private JCheckBox mtLevelupMoveSanityCheckBox;
    private JCheckBox mtKeepFieldMoveTutorsCheckBox;
    private JCheckBox mtForceGoodDamagingCheckBox;
    private JSlider mtForceGoodDamagingSlider;
    private JRadioButton mtcUnchangedRadioButton;
    private JRadioButton mtcRandomPreferSameTypeRadioButton;
    private JRadioButton mtcRandomCompletelyRadioButton;
    private JRadioButton mtcFullCompatibilityRadioButton;
    private JRadioButton fiUnchangedRadioButton;
    private JRadioButton fiShuffleRadioButton;
    private JRadioButton fiRandomRadioButton;
    private JRadioButton fiRandomEvenDistributionRadioButton;
    private JCheckBox fiBanBadItemsCheckBox;
    private JRadioButton shUnchangedRadioButton;
    private JRadioButton shShuffleRadioButton;
    private JRadioButton shRandomRadioButton;
    private JCheckBox shBanOverpoweredShopItemsCheckBox;
    private JCheckBox shBanBadItemsCheckBox;
    private JCheckBox shBanRegularShopItemsCheckBox;
    private JCheckBox shBalanceShopItemPricesCheckBox;
    private JCheckBox shGuaranteeEvolutionItemsCheckBox;
    private JCheckBox shGuaranteeXItemsCheckBox;
    private JCheckBox miscBWExpPatchCheckBox;
    private JCheckBox miscNerfXAccuracyCheckBox;
    private JCheckBox miscFixCritRateCheckBox;
    private JCheckBox miscFastestTextCheckBox;
    private JCheckBox miscRunningShoesIndoorsCheckBox;
    private JCheckBox miscRandomizePCPotionCheckBox;
    private JCheckBox miscAllowPikachuEvolutionCheckBox;
    private JCheckBox miscGiveNationalDexAtCheckBox;
    private JCheckBox miscUpdateTypeEffectivenessCheckBox;
    private JCheckBox miscLowerCasePokemonNamesCheckBox;
    private JCheckBox miscRandomizeCatchingTutorialCheckBox;
    private JCheckBox miscBanLuckyEggCheckBox;
    private JCheckBox miscNoFreeLuckyEggCheckBox;
    private JCheckBox miscBanBigMoneyManiacCheckBox;
    private JPanel pokemonAbilitiesPanel;
    private JPanel moveTutorPanel;
    private JPanel mtMovesPanel;
    private JPanel mtCompatPanel;
    private JLabel mtNoExistLabel;
    private JPanel shopItemsPanel;
    private JLabel mtNoneAvailableLabel;
    private JPanel miscTweaksPanel;
    private JLabel gameMascotLabel;
    private JPanel baseTweaksPanel;
    private JLabel romNameLabel;
    private JLabel romCodeLabel;
    private JLabel romSupportLabel;
    private JLabel websiteLinkLabel;
    private JCheckBox tmNoGameBreakingMovesCheckBox;
    private JCheckBox mtNoGameBreakingMovesCheckBox;
    private JCheckBox limitPokemonCheckBox;
    private JButton limitPokemonButton;
    private JCheckBox tpAllowAlternateFormesCheckBox;
    private JLabel versionLabel;
    private JCheckBox pbsFollowMegaEvosCheckBox;
    private JCheckBox paFollowMegaEvosCheckBox;
    private JCheckBox ptFollowMegaEvosCheckBox;
    private JCheckBox spAllowAltFormesCheckBox;
    private JCheckBox stpAllowAltFormesCheckBox;
    private JCheckBox stpSwapMegaEvosCheckBox;
    private JCheckBox tpSwapMegaEvosCheckBox;
    private JCheckBox wpAllowAltFormesCheckBox;
    private JCheckBox tpDoubleBattleModeCheckBox;
    private JCheckBox tpBossTrainersCheckBox;
    private JCheckBox tpImportantTrainersCheckBox;
    private JCheckBox tpRegularTrainersCheckBox;
    private JSpinner tpBossTrainersSpinner;
    private JSpinner tpImportantTrainersSpinner;
    private JSpinner tpRegularTrainersSpinner;
    private JLabel tpAdditionalPokemonForLabel;
    private JCheckBox peAllowAltFormesCheckBox;
    private JCheckBox miscSOSBattlesCheckBox;
    private JCheckBox tpRandomShinyTrainerPokemonCheckBox;
    private JRadioButton totpUnchangedRadioButton;
    private JRadioButton totpRandomRadioButton;
    private JRadioButton totpRandomSimilarStrengthRadioButton;
    private JRadioButton totpAllyUnchangedRadioButton;
    private JRadioButton totpAllyRandomRadioButton;
    private JRadioButton totpAllyRandomSimilarStrengthRadioButton;
    private JPanel totpAllyPanel;
    private JPanel totpAuraPanel;
    private JRadioButton totpAuraUnchangedRadioButton;
    private JRadioButton totpAuraRandomRadioButton;
    private JRadioButton totpAuraRandomSameStrengthRadioButton;
    private JCheckBox totpPercentageLevelModifierCheckBox;
    private JSlider totpPercentageLevelModifierSlider;
    private JCheckBox totpRandomizeHeldItemsCheckBox;
    private JCheckBox totpAllowAltFormesCheckBox;
    private JPanel totpPanel;
    private JCheckBox pmsEvolutionMovesCheckBox;
    private JComboBox<String> pbsUpdateComboBox;
    private JComboBox<String> mdUpdateComboBox;
    private JLabel wikiLinkLabel;
    private JCheckBox paWeighDuplicatesTogetherCheckBox;
    private JCheckBox miscBalanceStaticLevelsCheckBox;
    private JCheckBox miscRetainAltFormesCheckBox;
    private JComboBox pbsEXPCurveComboBox;
    private JCheckBox miscRunWithoutRunningShoesCheckBox;
    private JCheckBox peRemoveTimeBasedEvolutionsCheckBox;
    private JCheckBox tmFollowEvolutionsCheckBox;
    private JCheckBox mtFollowEvolutionsCheckBox;
    private JCheckBox stpPercentageLevelModifierCheckBox;
    private JSlider stpPercentageLevelModifierSlider;
    private JCheckBox stpFixMusicCheckBox;
    private JCheckBox miscFasterHPAndEXPBarsCheckBox;
    private JCheckBox tpBossTrainersItemsCheckBox;
    private JCheckBox tpImportantTrainersItemsCheckBox;
    private JCheckBox tpRegularTrainersItemsCheckBox;
    private JLabel tpHeldItemsLabel;
    private JCheckBox tpConsumableItemsOnlyCheckBox;
    private JCheckBox tpSensibleItemsCheckBox;
    private JCheckBox tpHighestLevelGetsItemCheckBox;
    private JPanel pickupItemsPanel;
    private JRadioButton puUnchangedRadioButton;
    private JRadioButton puRandomRadioButton;
    private JCheckBox puBanBadItemsCheckBox;
    private JCheckBox miscForceChallengeModeCheckBox;
    private JCheckBox pbsAssignEvoStatsRandomlyCheckBox;
    private JCheckBox noIrregularAltFormesCheckBox;
    private JRadioButton peRandomEveryLevelRadioButton;
    private JCheckBox miscFastDistortionWorldCheckBox;
    private JComboBox tpComboBox;
    private JCheckBox tpBetterMovesetsCheckBox;
    private JCheckBox paEnsureTwoAbilitiesCheckbox;

    private JPanel scriptingPanel;
    private RSyntaxTextArea sScriptInput;
    private JRadioButton stpScriptedRadioButton;
    private JRadioButton igtScriptedRadioButton;
    private JRadioButton wpScriptedRadioButton;
    private JCheckBox tpScriptedHeldItemsCheckBox;
    private JCheckBox limitPokemonScriptingCheckbox;
    private JCheckBox wpScriptHeldItemsCheckBox;
    private JCheckBox mdScriptedCheckBox;
    private JCheckBox pmsScriptLearntCheckBox;
    private JCheckBox pmsScriptEggCheckBox;
    private JCheckBox pmsScriptLearnAfterCheckBox;
    private JRadioButton pbsScriptedRadioButton;
    private JRadioButton ptScriptedRadioButton;
    private JRadioButton paScriptedRadioButton;
    private JRadioButton pbsUnchangedEXPCurveRadioButton;
    private JRadioButton pbsScriptedEXPCurveRadioButton;
    private RTextScrollPane sScriptInputScrollPane;
    private JRadioButton fiScriptedRadioButton;
    private JCheckBox fiShuffleItemsCheckBox;
    private JRadioButton puScriptedRadioButton;
    private JRadioButton tmScriptedRadioButton;
    private JRadioButton thcScriptedRadioButton;
    private JRadioButton mtcScriptedRadioButton;
    private JRadioButton mtScriptedRadioButton;
    private JButton ConsoleButton;
    private JRadioButton spUnchangedStarterHeldItemsRadioButton;
    private JRadioButton spScriptedStarterHeldItemsRadioButton;
    private JRadioButton peScriptedRadioButton;
    private JRadioButton shScriptedRadioButton;
    private JCheckBox shScriptedPricesCheckbox;

    private static JFrame frame;

    private static String launcherInput = "";
    public static boolean usedLauncher = false;

    private GenRestrictions currentRestrictions;
    private OperationDialog opDialog;

    private ResourceBundle bundle;
    protected RomHandler.Factory[] checkHandlers;
    private RomHandler romHandler;

    private boolean presetMode = false;
    private boolean initialPopup = true;
    private boolean showInvalidRomPopup = true;

    private List<JCheckBox> tweakCheckBoxes;
    private JPanel liveTweaksPanel = new JPanel();

    private JFileChooser romOpenChooser = new JFileChooser();
    private JFileChooser romSaveChooser = new JFileChooser();
    private JFileChooser qsOpenChooser = new JFileChooser();
    private JFileChooser qsSaveChooser = new JFileChooser();
    private JFileChooser qsUpdateChooser = new JFileChooser();
    private JFileChooser gameUpdateChooser = new JFileChooser();

    private JPopupMenu settingsMenu;
    private JMenuItem customNamesEditorMenuItem;
    private JMenuItem applyGameUpdateMenuItem;
    private JMenuItem removeGameUpdateMenuItem;
    private JMenuItem loadGetSettingsMenuItem;
    private JMenuItem keepOrUnloadGameAfterRandomizingMenuItem;

    private ImageIcon emptyIcon = new ImageIcon(getClass().getResource("/com/dabomstew/pkrandom/newgui/emptyIcon.png"));
    private boolean haveCheckedCustomNames, unloadGameOnSuccess;
    private Map<String, String> gameUpdates = new TreeMap<>();

    private List<String> trainerSettings = new ArrayList<>();
    private List<String> trainerSettingToolTips = new ArrayList<>();
    private final int TRAINER_UNCHANGED = 0, TRAINER_RANDOM = 1, TRAINER_RANDOM_EVEN = 2, TRAINER_RANDOM_EVEN_MAIN = 3,
                        TRAINER_TYPE_THEMED = 4, TRAINER_TYPE_THEMED_ELITE4_GYMS = 5, TRAINER_SCRIPTED = 6;

    private JFrame consoleWindow;
    private JTextArea consoleText;

    public NewRandomizerGUI() {
        ToolTipManager.sharedInstance().setInitialDelay(400);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        bundle = ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");
        testForRequiredConfigs();
        checkHandlers = new RomHandler.Factory[] { new Gen1RomHandler.Factory(), new Gen2RomHandler.Factory(),
                new Gen3RomHandler.Factory(), new Gen4RomHandler.Factory(), new Gen5RomHandler.Factory(),
                new Gen6RomHandler.Factory(), new Gen7RomHandler.Factory() };

        haveCheckedCustomNames = false;
        attemptReadConfig();
        initExplicit();
        initTweaksPanel();
        initFileChooserDirectories();

        boolean canWrite = attemptWriteConfig();
        if (!canWrite) {
            JOptionPane.showMessageDialog(null, bundle.getString("GUI.cantWriteConfigFile"));
        }

        if (!haveCheckedCustomNames) {
            checkCustomNames();
        }

        new Thread(() -> {
            String latestVersionString = "???";

            try {

                URL url = new URL(SysConstants.API_URL_ZX);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                String output;
                while ((output = br.readLine()) != null) {
                    String[] a = output.split("tag_name\":\"");
                    if (a.length > 1) {
                        latestVersionString = a[1].split("\",")[0];
                    }
                }

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // If the release version is newer than this version, bold it to make it more obvious.
            if (Version.isReleaseVersionNewer(latestVersionString)) {
                latestVersionString = String.format("<b>%s</b>", latestVersionString);
            }
            String finalLatestVersionString = latestVersionString;
            SwingUtilities.invokeLater(() -> {
                websiteLinkLabel.setText(String.format(bundle.getString("GUI.websiteLinkLabel.text"), finalLatestVersionString));
            });
        }).run();

        frame.setTitle(String.format(bundle.getString("GUI.windowTitle"),Version.VERSION_STRING));

        {
            consoleWindow = new JFrame("Console");

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(true);

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new FlowLayout());
            JButton button = new JButton("Clear");
            button.addActionListener(e -> {consoleText.setText("");System.out.flush();});

            consoleText = new JTextArea(20, 100);
            consoleText.setWrapStyleWord(false);
            consoleText.setEditable(false);
            consoleText.setFont(sScriptInput.getFont());

            JScrollPane scroller = new JScrollPane(consoleText);
            scroller.setOpaque(true);

            inputPanel.add(button);
            panel.add(inputPanel);
            panel.add(scroller);
            consoleWindow.getContentPane().add(BorderLayout.CENTER, panel);
            consoleWindow.pack();
            consoleWindow.setLocationByPlatform(true);
            consoleWindow.setResizable(true);

            MultiOutputStream multiOut= new MultiOutputStream(System.out, new TextAreaOutputStream(consoleText));
            PrintStream con=new PrintStream(multiOut);
            System.setOut(con);
            System.setErr(con);
        }

        openROMButton.addActionListener(e -> loadROM());
        ConsoleButton.addActionListener(e -> openConsoleWindow());
        pbsUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsShuffleRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsScriptedRadioButton.addActionListener(e -> addBaseStatsScriptFunc());
        pbsFollowMegaEvosCheckBox.addActionListener(e -> enableOrDisableSubControls());
        pbsFollowEvolutionsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        pbsStandardizeEXPCurvesRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsUnchangedEXPCurveRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsScriptedEXPCurveRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pbsScriptedEXPCurveRadioButton.addActionListener(e -> addPokemonEXPCurveScriptFunc());
        paUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        paRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        paScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        paScriptedRadioButton.addActionListener(e -> addPokemonAbilityScriptFunc());
        peUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        peRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        peRandomEveryLevelRadioButton.addActionListener(e -> enableOrDisableSubControls());
        peScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        peScriptedRadioButton.addActionListener(e -> addEvolutionScriptFunc());
        peAllowAltFormesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        spUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spCustomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spRandomTwoEvosRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spScriptedRadioButton.addActionListener(e -> addStarterScriptFunc());
        stpUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpSwapLegendariesSwapStandardsRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpScriptedRadioButton.addActionListener(e -> addStaticScriptFunc());
        stpRandomSimilarStrengthRadioButton.addActionListener(e -> enableOrDisableSubControls());
        stpPercentageLevelModifierCheckBox.addActionListener(e -> enableOrDisableSubControls());
        igtUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        igtRandomizeGivenPokemonOnlyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        igtRandomizeBothRequestedGivenRadioButton.addActionListener(e -> enableOrDisableSubControls());
        igtScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        igtScriptedRadioButton.addActionListener(e -> addTradeScriptFunc());
        pmsUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsRandomPreferringSameTypeRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsMetronomeOnlyModeRadioButton.addActionListener(e -> enableOrDisableSubControls());
        pmsGuaranteedLevel1MovesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        pmsForceGoodDamagingCheckBox.addActionListener(e -> enableOrDisableSubControls());
        pmsScriptLearntCheckBox.addActionListener(e -> addLearntMovesScriptFunc());
        pmsScriptLearnAfterCheckBox.addActionListener(e -> addAfterLearntMovesScriptFunc());
        pmsScriptEggCheckBox.addActionListener(e -> addEggMovesScriptFunc());
        tpForceFullyEvolvedAtCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpPercentageLevelModifierCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpEliteFourUniquePokemonCheckBox.addActionListener(e -> enableOrDisableSubControls());
        wpUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpArea1To1RadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpGlobal1To1RadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        wpScriptedRadioButton.addActionListener(e -> addWildEncounterScriptFunc());
        wpSetMinimumCatchRateCheckBox.addActionListener(e -> enableOrDisableSubControls());
        wpRandomizeHeldItemsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        wpScriptHeldItemsCheckBox.addActionListener(e -> addWildHeldItemScriptFunc());
        wpPercentageLevelModifierCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tmUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tmRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tmScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        tmScriptedRadioButton.addActionListener(e -> addTMScriptFunc());
        tmForceGoodDamagingCheckBox.addActionListener(e -> enableOrDisableSubControls());
        thcUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        thcRandomPreferSameTypeRadioButton.addActionListener(e -> enableOrDisableSubControls());
        thcRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        thcFullCompatibilityRadioButton.addActionListener(e -> enableOrDisableSubControls());
        thcScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        thcScriptedRadioButton.addActionListener(e -> addTMCompatFunc());
        mtUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtScriptedRadioButton.addActionListener(e -> addTutorScriptFunc());
        mtForceGoodDamagingCheckBox.addActionListener(e -> enableOrDisableSubControls());
        mtcUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtcRandomPreferSameTypeRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtcRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtcFullCompatibilityRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtcScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        mtcScriptedRadioButton.addActionListener(e -> addTutorCompatFunc());
        fiUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiShuffleRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiRandomEvenDistributionRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        fiScriptedRadioButton.addActionListener(e -> addFieldItemScriptFunc());
        fiShuffleRadioButton.addActionListener(e -> enableOrDisableSubControls());
        shUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        shShuffleRadioButton.addActionListener(e -> enableOrDisableSubControls());
        shRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        shScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        shScriptedRadioButton.addActionListener(e -> addShopItemScriptFunc());
        shScriptedPricesCheckbox.addActionListener(e -> enableOrDisableSubControls());
        shScriptedPricesCheckbox.addActionListener(e -> addShopItemPriceScriptFunc());
        puUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        puRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        puScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        puScriptedRadioButton.addActionListener(e -> addPickupItemScriptFunc());

        websiteLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = java.awt.Desktop.getDesktop();
                try {
                    desktop.browse(new URI(SysConstants.WEBSITE_URL_ZX));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        wikiLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = java.awt.Desktop.getDesktop();
                try {
                    desktop.browse(new URI(SysConstants.WIKI_URL_ZX));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        randomizeSaveButton.addActionListener(e -> saveROM());
        premadeSeedButton.addActionListener(e -> presetLoader());
        loadSettingsButton.addActionListener(e -> loadQS());
        saveSettingsButton.addActionListener(e -> saveQS());
        settingsButton.addActionListener(e -> settingsMenu.show(settingsButton,0,settingsButton.getHeight()));
        customNamesEditorMenuItem.addActionListener(e -> new CustomNamesEditorDialog(frame));
        applyGameUpdateMenuItem.addActionListener(e -> applyGameUpdateMenuItemActionPerformed());
        removeGameUpdateMenuItem.addActionListener(e -> removeGameUpdateMenuItemActionPerformed());
        loadGetSettingsMenuItem.addActionListener(e -> loadGetSettingsMenuItemActionPerformed());
        keepOrUnloadGameAfterRandomizingMenuItem.addActionListener(e -> keepOrUnloadGameAfterRandomizingMenuItemActionPerformed());
        limitPokemonButton.addActionListener(e -> {
            NewGenerationLimitDialog gld = new NewGenerationLimitDialog(frame, currentRestrictions,
                    romHandler.generationOfPokemon(), romHandler.forceSwapStaticMegaEvos());
            if (gld.pressedOK()) {
                currentRestrictions = gld.getChoice();
                if (currentRestrictions != null && !currentRestrictions.allowTrainerSwapMegaEvolvables(
                        romHandler.forceSwapStaticMegaEvos(), isTrainerSetting(TRAINER_TYPE_THEMED))) {
                    tpSwapMegaEvosCheckBox.setEnabled(false);
                    tpSwapMegaEvosCheckBox.setSelected(false);
                }
            }
        });
        limitPokemonCheckBox.addActionListener(e -> enableOrDisableSubControls());
        limitPokemonScriptingCheckbox.addActionListener(e -> enableOrDisableSubControls());
        limitPokemonScriptingCheckbox.addActionListener(e -> addPokemonLimitScriptFunc());
        tpAllowAlternateFormesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpBossTrainersCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpImportantTrainersCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpRegularTrainersCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpBossTrainersItemsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpScriptedHeldItemsCheckBox.addActionListener(e -> addTrainerHeldItemScriptFunc());
        tpImportantTrainersItemsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        tpRegularTrainersItemsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        totpUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpRandomSimilarStrengthRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpAllyUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpAllyRandomRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpAllyRandomSimilarStrengthRadioButton.addActionListener(e -> enableOrDisableSubControls());
        totpPercentageLevelModifierCheckBox.addActionListener(e -> enableOrDisableSubControls());
        pbsUpdateBaseStatsCheckBox.addActionListener(e -> enableOrDisableSubControls());
        mdUpdateMovesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        mdScriptedCheckBox.addActionListener(e -> addMoveDataScriptFunc());

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {

            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {
                showInitialPopup();
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
        ptUnchangedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        ptRandomFollowEvolutionsRadioButton.addActionListener(e -> enableOrDisableSubControls());
        ptRandomCompletelyRadioButton.addActionListener(e -> enableOrDisableSubControls());
        ptScriptedRadioButton.addActionListener(e -> enableOrDisableSubControls());
        ptScriptedRadioButton.addActionListener(e -> addPokemonTypeScriptFunc());
        spUnchangedStarterHeldItemsRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spRandomizeStarterHeldItemsRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spScriptedStarterHeldItemsRadioButton.addActionListener(e -> enableOrDisableSubControls());
        spScriptedStarterHeldItemsRadioButton.addActionListener(e -> addStarterHeldItemScriptFunc());
        tmLevelupMoveSanityCheckBox.addActionListener(e -> enableOrDisableSubControls());
        mtLevelupMoveSanityCheckBox.addActionListener(e -> enableOrDisableSubControls());
        noIrregularAltFormesCheckBox.addActionListener(e -> enableOrDisableSubControls());
        ptIsDualTypeCheckBox.addActionListener(e->enableOrDisableSubControls());
        tpComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableOrDisableSubControls();
                addTrainerScriptFunc();
            }
        });

        initializeScriptInput();
    }

    private class CustomFoldIcon implements Icon {
        private boolean collapsed;
        private int size;
        private int offset;
        private Color background;
        private Color armedBackground;
        private Color foreground;
        private boolean paintArmed;

        CustomFoldIcon(boolean collapsed, int size, int offset, Color foreground, Color background, Color armedBackground, boolean paintArmed) {
            this.collapsed = collapsed;
            this.size = size;
            this.offset = offset;
            this.foreground = foreground;
            this.background = background;
            this.armedBackground = armedBackground;
            this.paintArmed = paintArmed;
        }

        public int getIconHeight() {
            return size;
        }

        public int getIconWidth() {
            return size;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color bg = background;
            if (paintArmed && armedBackground != null) {
                bg = armedBackground;
            }

            g.setColor(bg);
            g.fillRect(x + offset, y, size, size);
            g.setColor(foreground);
            g.drawRect(x + offset, y, size, size);
            int border = size / 4;
            int length = size / 2;
            g.drawLine(x + border + offset, y + length, x + border + length + offset, y + length);
            if (this.collapsed) {
                g.drawLine(x + length + offset, y + border, x + length + offset, y + border + length);
            }

        }
    }

    private void initializeScriptInput()
    {
        //set highlighting style
        JythonTokenMaker.register();
        JythonSyntaxDocument jdoc = new JythonSyntaxDocument("text/jython");
        ScriptInstance.initJythonDoc(jdoc);
        Helper.initJythonDoc(jdoc);
        sScriptInput.setDocument(jdoc);
        sScriptInput.setSyntaxEditingStyle("text/jython");
        sScriptInput.setForeground(Color.white);
        sScriptInput.setBackground(new Color(50, 50, 50));
        SyntaxScheme scheme = sScriptInput.getSyntaxScheme();

        Font baseFont = scheme.getStyle(Token.COMMENT_EOL).font;
        Font boldFont = baseFont.deriveFont(Font.BOLD);
        Font italicFont = baseFont.deriveFont(Font.ITALIC);

        scheme.getStyle(Token.RESERVED_WORD).foreground = new Color(86, 156, 214);
        Color numberColor = new Color(181, 206, 168);
        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = numberColor;
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = numberColor;
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = numberColor;
        scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = new Color(204, 120, 50);
        scheme.getStyle(Token.LITERAL_BOOLEAN).font = boldFont;
        scheme.getStyle(Token.LITERAL_CHAR).foreground = new Color(211, 144, 116);
        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(211, 144, 116);
        scheme.getStyle(Token.VARIABLE).foreground = new Color(156, 220, 254);
        scheme.getStyle(Token.ANNOTATION).foreground = new Color(154, 154, 154);
        scheme.getStyle(Token.ANNOTATION).font = italicFont;
        scheme.getStyle(Token.DATA_TYPE).foreground = new Color(78, 201, 176);
        scheme.getStyle(Token.COMMENT_EOL).foreground = new Color(87, 166,74);
        scheme.getStyle(Token.COMMENT_EOL).font = italicFont;
        scheme.getStyle(Token.FUNCTION).foreground = new Color(219, 219, 114);
        scheme.getStyle(Token.OPERATOR).foreground = Color.white;
        scheme.getStyle(Token.MARKUP_CDATA).foreground = new Color(154, 154, 154); //use for arguments
        scheme.getStyle(Token.MARKUP_ENTITY_REFERENCE).foreground = new Color(190, 183, 255); //use for members
        scheme.getStyle(Token.PREPROCESSOR).foreground = new Color(182, 177, 40); //use for @staticmethod and such
        scheme.getStyle(Token.PREPROCESSOR).font = boldFont;

        sScriptInput.revalidate();

        sScriptInput.getDocument().addDocumentListener(new JythonDocumentListener(sScriptInput));

        /*
        commentStyle = Color(87, 166, 74) - italic
        keywordStyle = Color(86, 156, 214) - bold
        funcStyle = Color(220, 220, 170)
        stringStyle = Color(211, 144, 116)
        boolStyle = Color(204, 120, 50) - bold
        argStyle = Color(154, 154, 154)
        memberStyle = Color(190, 183, 255)
        numericLiteralStyle = Color(181, 206, 168)
        importStyle = Color(154, 154, 154) - italic - bold
        classStyle = Color(78, 201, 176) - bold
         */

        //enable code folding
        sScriptInput.setCodeFoldingEnabled(true);
        sScriptInputScrollPane.setFoldIndicatorEnabled(true);
        sScriptInputScrollPane.setLineNumbersEnabled(true);
        Gutter gutter = sScriptInputScrollPane.getGutter();
        gutter.setLineNumberFont(sScriptInput.getFont());
        final Color foldForeground = gutter.getFoldIndicatorForeground();
        final Color foldBackground = gutter.getFoldBackground();
        final Color foldArmed = gutter.getArmedFoldBackground();
        final int foldSize = 32;
        final int foldOffset = -24;
        gutter.setFoldIcons(new CustomFoldIcon(true, foldSize, foldOffset, foldForeground, foldBackground, foldArmed, true), new CustomFoldIcon(false, foldSize, foldOffset, foldForeground, foldBackground, foldArmed, true));
        gutter.setSpacingBetweenLineNumbersAndFoldIndicator(32);

        //add autocomplete
        /*CompletionProviderBase provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.setListCellRenderer(new CompletionCellRenderer());
        ac.setShowDescWindow(true);
        ac.setParameterAssistanceEnabled(true);
        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(300);
        provider.setAutoActivationRules(true, ".");
        ac.install(sScriptInput);*/

        //add line operations
        sScriptInput.addKeyListener(new JythonKeyListener(sScriptInput));

        //add the RandomSource import
        String scriptText = sScriptInput.getText();
        scriptText = addImport(scriptText, "com.dabomstew.pkrandom", "RandomSource");
        sScriptInput.setText(scriptText);
    }

    private CompletionProviderBase createCompletionProvider()
    {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        provider.addCompletion(new BasicCompletion(provider, "def"));
        provider.addCompletion(new BasicCompletion(provider, "class"));
        provider.addCompletion(new BasicCompletion(provider, "import"));
        provider.addCompletion(new BasicCompletion(provider, "from"));
        provider.addCompletion(new BasicCompletion(provider, "True"));
        provider.addCompletion(new BasicCompletion(provider, "False"));

        return provider;
    }

    private void showInitialPopup() {
        if (!usedLauncher) {
            String message = bundle.getString("GUI.pleaseUseTheLauncher");
            Object[] messages = {message};
            JOptionPane.showMessageDialog(frame, messages);
        }
        if (initialPopup) {
            String message = String.format(bundle.getString("GUI.firstStart"),Version.VERSION_STRING);
            JLabel label = new JLabel("<html><a href=\"https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Important-Information\">Checking out the \"Important Information\" page on the Wiki is highly recommended.</a>");
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Desktop desktop = java.awt.Desktop.getDesktop();
                    try {
                        desktop.browse(new URI("https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Important-Information"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            label.setCursor(new java.awt.Cursor(Cursor.HAND_CURSOR));
            Object[] messages = {message,label};
            JOptionPane.showMessageDialog(frame, messages);
            initialPopup = false;
            attemptWriteConfig();
        }
    }

    private void showInvalidRomPopup() {
        if (showInvalidRomPopup) {
            String message = String.format(bundle.getString("GUI.invalidRomMessage"));
            JLabel label = new JLabel("<html><b>Randomizing ROM hacks or bad ROM dumps is not supported and may cause issues.</b>");
            JCheckBox checkbox = new JCheckBox("Don't show this again");
            Object[] messages = {message, label, checkbox};
            Object[] options = {"OK"};
            JOptionPane.showOptionDialog(frame,
                    messages,
                    "Invalid ROM detected",
                    JOptionPane.OK_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    null);
            showInvalidRomPopup = !checkbox.isSelected();
            attemptWriteConfig();
        }
    }

    private void initFileChooserDirectories() {
        romOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        romSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        if (new File(SysConstants.ROOT_PATH + "settings/").exists()) {
            qsOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
            qsSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
            qsUpdateChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH + "settings/"));
        } else {
            qsOpenChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
            qsSaveChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
            qsUpdateChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));
        }
    }

    private void initExplicit() {

        versionLabel.setText(String.format(bundle.getString("GUI.versionLabel.text"), Version.VERSION_STRING));
        mtNoExistLabel.setVisible(false);
        mtNoneAvailableLabel.setVisible(false);
        baseTweaksPanel.add(liveTweaksPanel);
        liveTweaksPanel.setVisible(false);
        websiteLinkLabel.setCursor(new java.awt.Cursor(Cursor.HAND_CURSOR));
        wikiLinkLabel.setCursor(new java.awt.Cursor(Cursor.HAND_CURSOR));

        romOpenChooser.setFileFilter(new ROMFilter());

        romSaveChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        romSaveChooser.setFileFilter(new ROMFilter());

        qsOpenChooser.setFileFilter(new QSFileFilter());

        qsSaveChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        qsSaveChooser.setFileFilter(new QSFileFilter());

        qsUpdateChooser.setFileFilter(new QSFileFilter());

        settingsMenu = new JPopupMenu();

        SpinnerModel bossTrainerModel = new SpinnerNumberModel(
                1,
                1,
                5,
                1
        );
        SpinnerModel importantTrainerModel = new SpinnerNumberModel(
                1,
                1,
                5,
                1
        );
        SpinnerModel regularTrainerModel = new SpinnerNumberModel(
                1,
                1,
                5,
                1
        );

        SpinnerModel eliteFourUniquePokemonModel = new SpinnerNumberModel(
                1,
                1,
                2,
                1
        );

        List<String> keys = new ArrayList<>(bundle.keySet());
        Collections.sort(keys);
        for (String k: keys) {
            if (k.matches("^GUI\\.tpMain.*\\.text$")) {
                trainerSettings.add(bundle.getString(k));
                trainerSettingToolTips.add(k.replace("text","toolTipText"));
            }
        }

        tpBossTrainersSpinner.setModel(bossTrainerModel);
        tpImportantTrainersSpinner.setModel(importantTrainerModel);
        tpRegularTrainersSpinner.setModel(regularTrainerModel);
        tpEliteFourUniquePokemonSpinner.setModel(eliteFourUniquePokemonModel);

        customNamesEditorMenuItem = new JMenuItem();
        customNamesEditorMenuItem.setText(bundle.getString("GUI.customNamesEditorMenuItem.text"));
        settingsMenu.add(customNamesEditorMenuItem);

        loadGetSettingsMenuItem = new JMenuItem();
        loadGetSettingsMenuItem.setText(bundle.getString("GUI.loadGetSettingsMenuItem.text"));
        settingsMenu.add(loadGetSettingsMenuItem);

        applyGameUpdateMenuItem = new JMenuItem();
        applyGameUpdateMenuItem.setText(bundle.getString("GUI.applyGameUpdateMenuItem.text"));
        settingsMenu.add(applyGameUpdateMenuItem);

        removeGameUpdateMenuItem = new JMenuItem();
        removeGameUpdateMenuItem.setText(bundle.getString("GUI.removeGameUpdateMenuItem.text"));
        settingsMenu.add(removeGameUpdateMenuItem);

        keepOrUnloadGameAfterRandomizingMenuItem = new JMenuItem();
        if (this.unloadGameOnSuccess) {
            keepOrUnloadGameAfterRandomizingMenuItem.setText(bundle.getString("GUI.keepGameLoadedAfterRandomizingMenuItem.text"));
        } else {
            keepOrUnloadGameAfterRandomizingMenuItem.setText(bundle.getString("GUI.unloadGameAfterRandomizingMenuItem.text"));
        }
        settingsMenu.add(keepOrUnloadGameAfterRandomizingMenuItem);
    }

    private void loadROM() {
        romOpenChooser.setSelectedFile(null);
        int returnVal = romOpenChooser.showOpenDialog(mainPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File fh = romOpenChooser.getSelectedFile();
            try {
                Utils.validateRomFile(fh);
            } catch (Utils.InvalidROMException e) {
                switch (e.getType()) {
                    case LENGTH:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.tooShortToBeARom"), fh.getName()));
                        return;
                    case ZIP_FILE:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.openedZIPfile"), fh.getName()));
                        return;
                    case RAR_FILE:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.openedRARfile"), fh.getName()));
                        return;
                    case IPS_FILE:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.openedIPSfile"), fh.getName()));
                        return;
                    case UNREADABLE:
                        JOptionPane.showMessageDialog(mainPanel,
                                String.format(bundle.getString("GUI.unreadableRom"), fh.getName()));
                        return;
                }
            }

            for (RomHandler.Factory rhf : checkHandlers) {
                if (rhf.isLoadable(fh.getAbsolutePath())) {
                    this.romHandler = rhf.create(RandomSource.instance());
                    if (!usedLauncher && this.romHandler instanceof Abstract3DSRomHandler) {
                        String message = bundle.getString("GUI.pleaseUseTheLauncher");
                        Object[] messages = {message};
                        JOptionPane.showMessageDialog(frame, messages);
                        this.romHandler = null;
                        return;
                    }
                    opDialog = new OperationDialog(bundle.getString("GUI.loadingText"), frame, true);
                    Thread t = new Thread(() -> {
                        boolean romLoaded = false;
                        SwingUtilities.invokeLater(() -> opDialog.setVisible(true));
                        try {
                            this.romHandler.loadRom(fh.getAbsolutePath());
                            if (gameUpdates.containsKey(this.romHandler.getROMCode())) {
                                this.romHandler.loadGameUpdate(gameUpdates.get(this.romHandler.getROMCode()));
                            }
                            romLoaded = true;
                        } catch (EncryptedROMException ex) {
                            JOptionPane.showMessageDialog(mainPanel,
                                    String.format(bundle.getString("GUI.encryptedRom"), fh.getAbsolutePath()));
                        } catch (Exception ex) {
                            attemptToLogException(ex, "GUI.loadFailed", "GUI.loadFailedNoLog", null, null);
                        }
                        final boolean loadSuccess = romLoaded;
                        SwingUtilities.invokeLater(() -> {
                            this.opDialog.setVisible(false);
                            this.initialState();
                            if (loadSuccess) {
                                this.romLoaded();
                            }
                        });
                    });
                    t.start();

                    return;
                }
            }
            JOptionPane.showMessageDialog(mainPanel,
                    String.format(bundle.getString("GUI.unsupportedRom"), fh.getName()));
        }
    }

    private void saveROM() {
        if (romHandler == null) {
            return; // none loaded
        }
        if (raceModeCheckBox.isSelected() && isTrainerSetting(TRAINER_UNCHANGED) && wpUnchangedRadioButton.isSelected()) {
            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.raceModeRequirements"));
            return;
        }
        if (limitPokemonCheckBox.isSelected()
                && (this.currentRestrictions == null || this.currentRestrictions.nothingSelected())) {
            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.pokeLimitNotChosen"));
            return;
        }
        SaveType outputType = askForSaveType();
        romSaveChooser.setSelectedFile(null);
        boolean allowed = false;
        File fh = null;
        if (outputType == SaveType.FILE) {
            romSaveChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = romSaveChooser.showSaveDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fh = romSaveChooser.getSelectedFile();
                // Fix or add extension
                List<String> extensions = new ArrayList<>(Arrays.asList("sgb", "gbc", "gba", "nds", "cxi"));
                extensions.remove(this.romHandler.getDefaultExtension());
                fh = FileFunctions.fixFilename(fh, this.romHandler.getDefaultExtension(), extensions);
                allowed = true;
                if (this.romHandler instanceof AbstractDSRomHandler || this.romHandler instanceof Abstract3DSRomHandler) {
                    String currentFN = this.romHandler.loadedFilename();
                    if (currentFN.equals(fh.getAbsolutePath())) {
                        JOptionPane.showMessageDialog(frame, bundle.getString("GUI.cantOverwriteDS"));
                        allowed = false;
                    }
                }
            }
        } else if (outputType == SaveType.DIRECTORY) {
            romSaveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = romSaveChooser.showSaveDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fh = romSaveChooser.getSelectedFile();
                allowed = true;
            }
        }

        if (allowed && fh != null) {
            // Get a seed
            long seed = RandomSource.pickSeed();
            // Apply it
            RandomSource.seed(seed);
            presetMode = false;

            try {
                CustomNamesSet cns = FileFunctions.getCustomNames();
                performRandomization(fh.getAbsolutePath(), seed, cns, outputType == SaveType.DIRECTORY);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, bundle.getString("GUI.cantLoadCustomNames"));
            }

        }
    }

    private void loadQS() {
        if (this.romHandler == null) {
            return;
        }
        qsOpenChooser.setSelectedFile(null);
        int returnVal = qsOpenChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = qsOpenChooser.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(fh);
                Settings settings = Settings.read(fis);
                fis.close();

                SwingUtilities.invokeLater(() -> {
                    // load settings
                    initialState();
                    romLoaded();
                    Settings.TweakForROMFeedback feedback = settings.tweakForRom(this.romHandler);
                    if (feedback.isChangedStarter() && settings.getStartersMod() == Settings.StartersMod.CUSTOM) {
                        JOptionPane.showMessageDialog(frame, bundle.getString("GUI.starterUnavailable"));
                    }
                    this.restoreStateFromSettings(settings);

                    if (settings.isUpdatedFromOldVersion()) {
                        // show a warning dialog, but load it
                        JOptionPane.showMessageDialog(frame, bundle.getString("GUI.settingsFileOlder"));
                    }

                    JOptionPane.showMessageDialog(frame,
                            String.format(bundle.getString("GUI.settingsLoaded"), fh.getName()));
                });
            } catch (UnsupportedOperationException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, bundle.getString("GUI.invalidSettingsFile"));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, bundle.getString("GUI.settingsLoadFailed"));
            }
        }
    }

    private void saveQS() {
        if (this.romHandler == null) {
            return;
        }
        qsSaveChooser.setSelectedFile(null);
        int returnVal = qsSaveChooser.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = qsSaveChooser.getSelectedFile();
            // Fix or add extension
            fh = FileFunctions.fixFilename(fh, "rnqs");
            // Save now?
            try {
                FileOutputStream fos = new FileOutputStream(fh);
                getCurrentSettings().write(fos);
                fos.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, bundle.getString("GUI.settingsSaveFailed"));
            }
        }
    }

    private void performRandomization(final String filename, final long seed, CustomNamesSet customNames, boolean saveAsDirectory) {
        final Settings settings = createSettingsFromState(customNames);
        final boolean raceMode = settings.isRaceMode();
        // Setup verbose log
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream log;
        try {
            log = new PrintStream(baos, false, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log = new PrintStream(baos);
        }

        final PrintStream verboseLog = log;

        try {
            final AtomicInteger finishedCV = new AtomicInteger(0);
            opDialog = new OperationDialog(bundle.getString("GUI.savingText"), frame, true);
            Thread t = new Thread(() -> {
                SwingUtilities.invokeLater(() -> opDialog.setVisible(true));
                boolean succeededSave = false;
                try {
                    romHandler.setLog(verboseLog);
                    finishedCV.set(new Randomizer(settings, romHandler, bundle, saveAsDirectory).randomize(filename,
                            verboseLog, seed));
                    succeededSave = true;
                } catch (RandomizationException ex) {
                    attemptToLogException(ex, "GUI.saveFailedMessage",
                            "GUI.saveFailedMessageNoLog", true, settings.toString(), Long.toString(seed));
                    if (verboseLog != null) {
                        verboseLog.close();
                    }
                } catch (Exception ex) {
                    attemptToLogException(ex, "GUI.saveFailedIO", "GUI.saveFailedIONoLog", settings.toString(), Long.toString(seed));
                    if (verboseLog != null) {
                        verboseLog.close();
                    }
                }
                if (succeededSave) {
                    SwingUtilities.invokeLater(() -> {
                        opDialog.setVisible(false);
                        // Log?
                        verboseLog.close();
                        byte[] out = baos.toByteArray();

                        if (raceMode) {
                            JOptionPane.showMessageDialog(frame,
                                    String.format(bundle.getString("GUI.raceModeCheckValuePopup"),
                                            finishedCV.get()));
                        } else {
                            int response = JOptionPane.showConfirmDialog(frame,
                                    bundle.getString("GUI.saveLogDialog.text"),
                                    bundle.getString("GUI.saveLogDialog.title"),
                                    JOptionPane.YES_NO_OPTION);
                            if (response == JOptionPane.YES_OPTION) {
                                try {
                                    FileOutputStream fos = new FileOutputStream(filename + ".log");
                                    fos.write(0xEF);
                                    fos.write(0xBB);
                                    fos.write(0xBF);
                                    fos.write(out);
                                    fos.close();
                                } catch (IOException e) {
                                    JOptionPane.showMessageDialog(frame,
                                            bundle.getString("GUI.logSaveFailed"));
                                    return;
                                }
                                JOptionPane.showMessageDialog(frame,
                                        String.format(bundle.getString("GUI.logSaved"), filename));
                            }
                        }
                        if (presetMode) {
                            JOptionPane.showMessageDialog(frame,
                                    bundle.getString("GUI.randomizationDone"));
                            // Done
                            if (this.unloadGameOnSuccess) {
                                romHandler = null;
                                initialState();
                            } else {
                                reinitializeRomHandler();
                            }
                        } else {
                            // Compile a config string
                            try {
                                String configString = getCurrentSettings().toString();
                                // Show the preset maker
                                new PresetMakeDialog(frame, seed, configString);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(frame,
                                        bundle.getString("GUI.cantLoadCustomNames"));
                            }

                            // Done
                            if (this.unloadGameOnSuccess) {
                                romHandler = null;
                                initialState();
                            } else {
                                reinitializeRomHandler();
                            }
                        }
                    });
                } else {
                    boolean shouldReset = resetOnFail();
                    SwingUtilities.invokeLater(() -> {
                        opDialog.setVisible(false);
                        if(shouldReset)
                        {
                            romHandler = null;
                            initialState();
                        }
                    });
                }
            });
            t.start();
        } catch (Exception ex) {
            attemptToLogException(ex, "GUI.saveFailed", "GUI.saveFailedNoLog", settings.toString(), Long.toString(seed));
            if (verboseLog != null) {
                verboseLog.close();
            }
        }
    }

    private void presetLoader() {
        PresetLoadDialog pld = new PresetLoadDialog(this,frame);
        if (pld.isCompleted()) {
            // Apply it
            long seed = pld.getSeed();
            String config = pld.getConfigString();
            this.romHandler = pld.getROM();
            if (gameUpdates.containsKey(this.romHandler.getROMCode())) {
                this.romHandler.loadGameUpdate(gameUpdates.get(this.romHandler.getROMCode()));
            }
            this.romLoaded();
            Settings settings;
            try {
                settings = Settings.fromString(config);
                settings.tweakForRom(this.romHandler);
                this.restoreStateFromSettings(settings);
            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                // settings load failed
                e.printStackTrace();
                this.romHandler = null;
                initialState();
            }
            SaveType outputType = askForSaveType();
            romSaveChooser.setSelectedFile(null);
            boolean allowed = false;
            File fh = null;
            if (outputType == SaveType.FILE) {
                romSaveChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = romSaveChooser.showSaveDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fh = romSaveChooser.getSelectedFile();
                    // Fix or add extension
                    List<String> extensions = new ArrayList<>(Arrays.asList("sgb", "gbc", "gba", "nds", "cxi"));
                    extensions.remove(this.romHandler.getDefaultExtension());
                    fh = FileFunctions.fixFilename(fh, this.romHandler.getDefaultExtension(), extensions);
                    allowed = true;
                    if (this.romHandler instanceof AbstractDSRomHandler || this.romHandler instanceof Abstract3DSRomHandler) {
                        String currentFN = this.romHandler.loadedFilename();
                        if (currentFN.equals(fh.getAbsolutePath())) {
                            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.cantOverwriteDS"));
                            allowed = false;
                        }
                    }
                } else {
                    this.romHandler = null;
                    initialState();
                }
            } else if (outputType == SaveType.DIRECTORY) {
                romSaveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = romSaveChooser.showSaveDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fh = romSaveChooser.getSelectedFile();
                    allowed = true;
                } else {
                    this.romHandler = null;
                    initialState();
                }
            }

            if (allowed && fh != null) {
                // Apply the seed we were given
                RandomSource.seed(seed);
                presetMode = true;
                performRandomization(fh.getAbsolutePath(), seed, pld.getCustomNames(), outputType == SaveType.DIRECTORY);
            }
        }

    }

    private void createUIComponents() {

    }


    private enum SaveType {
        FILE, DIRECTORY, INVALID
    }

    private SaveType askForSaveType() {
        SaveType saveType = SaveType.FILE;
        if (romHandler.hasGameUpdateLoaded()) {
            String text = bundle.getString("GUI.savingWithGameUpdate");
            String url = "https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Randomizing-the-3DS-games#managing-game-updates";
            showMessageDialogWithLink(text, url);
            saveType = SaveType.DIRECTORY;
        } else if (romHandler.generationOfPokemon() == 6 || romHandler.generationOfPokemon() == 7) {
            Object[] options3DS = {"CXI", "LayeredFS"};
            String question = "Would you like to output your 3DS game as a CXI file or as a LayeredFS directory?";
            JLabel label = new JLabel("<html><a href=\"https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Randomizing-the-3DS-games#changes-to-saving-a-rom-when-working-with-3ds-games\">For more information, click here.</a>");
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Desktop desktop = java.awt.Desktop.getDesktop();
                    try {
                        desktop.browse(new URI("https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Randomizing-the-3DS-games#changes-to-saving-a-rom-when-working-with-3ds-games"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            label.setCursor(new java.awt.Cursor(Cursor.HAND_CURSOR));
            Object[] messages = {question,label};
            int returnVal3DS = JOptionPane.showOptionDialog(frame,
                    messages,
                    "3DS Output Choice",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options3DS,
                    null);
            if (returnVal3DS < 0) {
                saveType = SaveType.INVALID;
            } else {
                saveType = SaveType.values()[returnVal3DS];
            }
        }
        return saveType;
    }

    private void applyGameUpdateMenuItemActionPerformed() {

        if (romHandler == null) return;

        gameUpdateChooser.setSelectedFile(null);
        gameUpdateChooser.setFileFilter(new GameUpdateFilter());
        int returnVal = gameUpdateChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = gameUpdateChooser.getSelectedFile();

            // On the 3DS, the update has the same title ID as the base game, save for the 8th character,
            // which is 'E' instead of '0'. We can use this to detect if the update matches the game.
            String actualUpdateTitleId = Abstract3DSRomHandler.getTitleIdFromFile(fh.getAbsolutePath());
            if (actualUpdateTitleId == null) {
                // Error: couldn't find a title ID in the update
                JOptionPane.showMessageDialog(frame, String.format(bundle.getString("GUI.invalidGameUpdate"), fh.getName()));
                return;
            }
            Abstract3DSRomHandler ctrRomHandler = (Abstract3DSRomHandler) romHandler;
            String baseGameTitleId = ctrRomHandler.getTitleIdFromLoadedROM();
            char[] baseGameTitleIdChars = baseGameTitleId.toCharArray();
            baseGameTitleIdChars[7] = 'E';
            String expectedUpdateTitleId = String.valueOf(baseGameTitleIdChars);
            if (actualUpdateTitleId.equals(expectedUpdateTitleId)) {
                try {
                    romHandler.loadGameUpdate(fh.getAbsolutePath());
                } catch (EncryptedROMException ex) {
                    JOptionPane.showMessageDialog(mainPanel,
                            String.format(bundle.getString("GUI.encryptedRom"), fh.getAbsolutePath()));
                    return;
                }
                gameUpdates.put(romHandler.getROMCode(), fh.getAbsolutePath());
                attemptWriteConfig();
                removeGameUpdateMenuItem.setVisible(true);
                setRomNameLabel();
                String text = String.format(bundle.getString("GUI.gameUpdateApplied"), romHandler.getROMName());
                String url = "https://github.com/Ajarmar/universal-pokemon-randomizer-zx/wiki/Randomizing-the-3DS-games#3ds-game-updates";
                showMessageDialogWithLink(text, url);
            } else {
                // Error: update is not for the correct game
                JOptionPane.showMessageDialog(frame, String.format(bundle.getString("GUI.nonMatchingGameUpdate"), fh.getName(), romHandler.getROMName()));
            }
        }
    }

    private void removeGameUpdateMenuItemActionPerformed() {

        if (romHandler == null) return;

        gameUpdates.remove(romHandler.getROMCode());
        attemptWriteConfig();
        romHandler.removeGameUpdate();
        removeGameUpdateMenuItem.setVisible(false);
        setRomNameLabel();
    }

    private void loadGetSettingsMenuItemActionPerformed() {

        if (romHandler == null) return;

        String currentSettingsString = "Current Settings String:";
        JTextField currentSettingsStringField = new JTextField();
        currentSettingsStringField.setEditable(false);
        try {
            String theSettingsString = Version.VERSION + getCurrentSettings().toString();
            currentSettingsStringField.setColumns(Settings.LENGTH_OF_SETTINGS_DATA * 2);
            currentSettingsStringField.setText(theSettingsString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String loadSettingsString = "Load Settings String:";
        JTextField loadSettingsStringField = new JTextField();
        Object[] messages = {currentSettingsString,currentSettingsStringField,loadSettingsString,loadSettingsStringField};
        Object[] options = {"Load","Cancel"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                messages,
                "Get/Load Settings String",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null
        );
        if (choice == 0) {
            String configString = loadSettingsStringField.getText().trim();
            if (configString.length() > 0) {
                if (configString.length() < 3) {
                    JOptionPane.showMessageDialog(frame,bundle.getString("GUI.invalidSettingsString"));
                } else {
                    try {
                        int settingsStringVersionNumber = Integer.parseInt(configString.substring(0, 3));
                        if (settingsStringVersionNumber < Version.VERSION) {
                            JOptionPane.showMessageDialog(frame,bundle.getString("GUI.settingsStringOlder"));
                            String updatedSettingsString = new SettingsUpdater().update(settingsStringVersionNumber, configString.substring(3));
                            Settings settings = Settings.fromString(updatedSettingsString);
                            settings.tweakForRom(this.romHandler);
                            restoreStateFromSettings(settings);
                            JOptionPane.showMessageDialog(frame,bundle.getString("GUI.settingsStringLoaded"));
                        } else if (settingsStringVersionNumber > Version.VERSION) {
                            JOptionPane.showMessageDialog(frame,bundle.getString("GUI.settingsStringTooNew"));
                        } else {
                            Settings settings = Settings.fromString(configString.substring(3));
                            settings.tweakForRom(this.romHandler);
                            restoreStateFromSettings(settings);
                            JOptionPane.showMessageDialog(frame,bundle.getString("GUI.settingsStringLoaded"));
                        }
                    } catch (UnsupportedEncodingException | IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(frame,bundle.getString("GUI.invalidSettingsString"));
                    }
                }

            }
        }
    }

    private void keepOrUnloadGameAfterRandomizingMenuItemActionPerformed() {
        this.unloadGameOnSuccess = !this.unloadGameOnSuccess;
        if (this.unloadGameOnSuccess) {
            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.unloadGameAfterRandomizing"));
            keepOrUnloadGameAfterRandomizingMenuItem.setText(bundle.getString("GUI.keepGameLoadedAfterRandomizingMenuItem.text"));
        } else {
            JOptionPane.showMessageDialog(frame, bundle.getString("GUI.keepGameLoadedAfterRandomizing"));
            keepOrUnloadGameAfterRandomizingMenuItem.setText(bundle.getString("GUI.unloadGameAfterRandomizingMenuItem.text"));
        }
        attemptWriteConfig();
    }

    private void showMessageDialogWithLink(String text, String url) {
        JLabel label = new JLabel("<html><a href=\"" + url + "\">For more information, click here.</a>");
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = java.awt.Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        label.setCursor(new java.awt.Cursor(Cursor.HAND_CURSOR));
        Object[] messages = {text,label};
        JOptionPane.showMessageDialog(frame, messages);
    }

    // This is only intended to be used with the "Keep Game Loaded After Randomizing" setting; it assumes that
    // the game has already been loaded once, and we just need to reload the same game to reinitialize the
    // RomHandler. Don't use this for other purposes unless you know what you're doing.
    private void reinitializeRomHandler() {
        String currentFN = this.romHandler.loadedFilename();
        for (RomHandler.Factory rhf : checkHandlers) {
            if (rhf.isLoadable(currentFN)) {
                this.romHandler = rhf.create(RandomSource.instance());
                opDialog = new OperationDialog(bundle.getString("GUI.loadingText"), frame, true);
                Thread t = new Thread(() -> {
                    SwingUtilities.invokeLater(() -> opDialog.setVisible(true));
                    try {
                        this.romHandler.loadRom(currentFN);
                        if (gameUpdates.containsKey(this.romHandler.getROMCode())) {
                            this.romHandler.loadGameUpdate(gameUpdates.get(this.romHandler.getROMCode()));
                        }
                    } catch (Exception ex) {
                        attemptToLogException(ex, "GUI.loadFailed", "GUI.loadFailedNoLog", null, null);
                    }
                    SwingUtilities.invokeLater(() -> {
                        this.opDialog.setVisible(false);
                    });
                });
                t.start();

                return;
            }
        }
    }

    private void restoreStateFromSettings(Settings settings) {

        limitPokemonCheckBox.setSelected(settings.isLimitPokemon());
        limitPokemonScriptingCheckbox.setSelected(settings.isScriptedPokemonLimit());
        currentRestrictions = settings.getCurrentRestrictions();
        if (currentRestrictions != null) {
            currentRestrictions.limitToGen(romHandler.generationOfPokemon());
        }
        noIrregularAltFormesCheckBox.setSelected(settings.isBanIrregularAltFormes());
        raceModeCheckBox.setSelected(settings.isRaceMode());

        peChangeImpossibleEvosCheckBox.setSelected(settings.isChangeImpossibleEvolutions());
        mdUpdateMovesCheckBox.setSelected(settings.isUpdateMoves());
        mdUpdateComboBox.setSelectedIndex(Math.max(0,settings.getUpdateMovesToGeneration() - (romHandler.generationOfPokemon()+1)));
        tpRandomizeTrainerNamesCheckBox.setSelected(settings.isRandomizeTrainerNames());
        tpRandomizeTrainerClassNamesCheckBox.setSelected(settings.isRandomizeTrainerClassNames());
        ptIsDualTypeCheckBox.setSelected(settings.isDualTypeOnly());

        pbsScriptedRadioButton.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.SCRIPTED);
        pbsRandomRadioButton.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.RANDOM);
        pbsShuffleRadioButton.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.SHUFFLE);
        pbsUnchangedRadioButton.setSelected(settings.getBaseStatisticsMod() == Settings.BaseStatisticsMod.UNCHANGED);
        pbsFollowEvolutionsCheckBox.setSelected(settings.isBaseStatsFollowEvolutions());
        pbsUpdateBaseStatsCheckBox.setSelected(settings.isUpdateBaseStats());
        pbsUpdateComboBox.setSelectedIndex(Math.max(0,settings.getUpdateBaseStatsToGeneration() - (Math.max(6,romHandler.generationOfPokemon()+1))));
        pbsStandardizeEXPCurvesRadioButton.setSelected(settings.isStandardizeEXPCurves());
        pbsScriptedEXPCurveRadioButton.setSelected(settings.isScriptEXPCurves());
        pbsUnchangedEXPCurveRadioButton.setSelected(!pbsStandardizeEXPCurvesRadioButton.isSelected() && !pbsScriptedEXPCurveRadioButton.isSelected());
        pbsLegendariesSlowRadioButton.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.LEGENDARIES);
        pbsStrongLegendariesSlowRadioButton.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.STRONG_LEGENDARIES);
        pbsAllMediumFastRadioButton.setSelected(settings.getExpCurveMod() == Settings.ExpCurveMod.ALL);
        ExpCurve[] expCurves = getEXPCurvesForGeneration(romHandler.generationOfPokemon());
        int index = 0;
        for (int i = 0; i < expCurves.length; i++) {
            if (expCurves[i] == settings.getSelectedEXPCurve()) {
                index = i;
            }
        }
        pbsEXPCurveComboBox.setSelectedIndex(index);
        pbsFollowMegaEvosCheckBox.setSelected(settings.isBaseStatsFollowMegaEvolutions());
        pbsAssignEvoStatsRandomlyCheckBox.setSelected(settings.isAssignEvoStatsRandomly());

        paUnchangedRadioButton.setSelected(settings.getAbilitiesMod() == Settings.AbilitiesMod.UNCHANGED);
        paRandomRadioButton.setSelected(settings.getAbilitiesMod() == Settings.AbilitiesMod.RANDOMIZE);
        paScriptedRadioButton.setSelected(settings.getAbilitiesMod() == Settings.AbilitiesMod.SCRIPTED);
        paAllowWonderGuardCheckBox.setSelected(settings.isAllowWonderGuard());
        paFollowEvolutionsCheckBox.setSelected(settings.isAbilitiesFollowEvolutions());
        paTrappingAbilitiesCheckBox.setSelected(settings.isBanTrappingAbilities());
        paNegativeAbilitiesCheckBox.setSelected(settings.isBanNegativeAbilities());
        paBadAbilitiesCheckBox.setSelected(settings.isBanBadAbilities());
        paFollowMegaEvosCheckBox.setSelected(settings.isAbilitiesFollowMegaEvolutions());
        paWeighDuplicatesTogetherCheckBox.setSelected(settings.isWeighDuplicateAbilitiesTogether());
        paEnsureTwoAbilitiesCheckbox.setSelected(settings.isEnsureTwoAbilities());

        ptScriptedRadioButton.setSelected(settings.getTypesMod() == Settings.TypesMod.SCRIPTED);
        ptRandomFollowEvolutionsRadioButton.setSelected(settings.getTypesMod() == Settings.TypesMod.RANDOM_FOLLOW_EVOLUTIONS);
        ptRandomCompletelyRadioButton.setSelected(settings.getTypesMod() == Settings.TypesMod.COMPLETELY_RANDOM);
        ptUnchangedRadioButton.setSelected(settings.getTypesMod() == Settings.TypesMod.UNCHANGED);
        ptFollowMegaEvosCheckBox.setSelected(settings.isTypesFollowMegaEvolutions());
        pmsNoGameBreakingMovesCheckBox.setSelected(settings.doBlockBrokenMoves());

        peMakeEvolutionsEasierCheckBox.setSelected(settings.isMakeEvolutionsEasier());
        peRemoveTimeBasedEvolutionsCheckBox.setSelected(settings.isRemoveTimeBasedEvolutions());

        spCustomRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.CUSTOM);
        spRandomCompletelyRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.COMPLETELY_RANDOM);
        spUnchangedRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.UNCHANGED);
        spRandomTwoEvosRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.RANDOM_WITH_TWO_EVOLUTIONS);
        spScriptedRadioButton.setSelected(settings.getStartersMod() == Settings.StartersMod.SCRIPTED);
        spUnchangedStarterHeldItemsRadioButton.setSelected(!settings.isRandomizeStartersHeldItems() && !settings.isScriptStarterHeldItems());
        spRandomizeStarterHeldItemsRadioButton.setSelected(settings.isRandomizeStartersHeldItems());
        spScriptedStarterHeldItemsRadioButton.setSelected(settings.isScriptStarterHeldItems());
        spBanBadItemsCheckBox.setSelected(settings.isBanBadRandomStarterHeldItems());
        spAllowAltFormesCheckBox.setSelected(settings.isAllowStarterAltFormes());

        int[] customStarters = settings.getCustomStarters();
        spComboBox1.setSelectedIndex(customStarters[0] - 1);
        spComboBox2.setSelectedIndex(customStarters[1] - 1);
        spComboBox3.setSelectedIndex(customStarters[2] - 1);

        peUnchangedRadioButton.setSelected(settings.getEvolutionsMod() == Settings.EvolutionsMod.UNCHANGED);
        peRandomRadioButton.setSelected(settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM);
        peRandomEveryLevelRadioButton.setSelected(settings.getEvolutionsMod() == Settings.EvolutionsMod.RANDOM_EVERY_LEVEL);
        peScriptedRadioButton.setSelected(settings.getEvolutionsMod() == Settings.EvolutionsMod.SCRIPTED);
        peSimilarStrengthCheckBox.setSelected(settings.isEvosSimilarStrength());
        peSameTypingCheckBox.setSelected(settings.isEvosSameTyping());
        peLimitEvolutionsToThreeCheckBox.setSelected(settings.isEvosMaxThreeStages());
        peForceChangeCheckBox.setSelected(settings.isEvosForceChange());
        peAllowAltFormesCheckBox.setSelected(settings.isEvosAllowAltFormes());

        mdRandomizeMoveAccuracyCheckBox.setSelected(settings.isRandomizeMoveAccuracies());
        mdRandomizeMoveCategoryCheckBox.setSelected(settings.isRandomizeMoveCategory());
        mdRandomizeMovePowerCheckBox.setSelected(settings.isRandomizeMovePowers());
        mdRandomizeMovePPCheckBox.setSelected(settings.isRandomizeMovePPs());
        mdRandomizeMoveTypesCheckBox.setSelected(settings.isRandomizeMoveTypes());
        mdScriptedCheckBox.setSelected(settings.isScriptMoveData());

        pmsRandomCompletelyRadioButton.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.COMPLETELY_RANDOM);
        pmsRandomPreferringSameTypeRadioButton.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.RANDOM_PREFER_SAME_TYPE);
        pmsUnchangedRadioButton.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.UNCHANGED);
        pmsMetronomeOnlyModeRadioButton.setSelected(settings.getMovesetsMod() == Settings.MovesetsMod.METRONOME_ONLY);
        pmsGuaranteedLevel1MovesCheckBox.setSelected(settings.isStartWithGuaranteedMoves());
        pmsGuaranteedLevel1MovesSlider.setValue(settings.getGuaranteedMoveCount());
        pmsReorderDamagingMovesCheckBox.setSelected(settings.isReorderDamagingMoves());
        pmsForceGoodDamagingCheckBox.setSelected(settings.isMovesetsForceGoodDamaging());
        pmsForceGoodDamagingSlider.setValue(settings.getMovesetsGoodDamagingPercent());
        pmsNoGameBreakingMovesCheckBox.setSelected(settings.isBlockBrokenMovesetMoves());
        pmsEvolutionMovesCheckBox.setSelected(settings.isEvolutionMovesForAll());
        pmsScriptLearntCheckBox.setSelected(settings.isScriptLearntMoves());
        pmsScriptLearnAfterCheckBox.setSelected(settings.isScriptLearntMoves());
        pmsScriptEggCheckBox.setSelected(settings.isScriptEggMoves());

        tpSimilarStrengthCheckBox.setSelected(settings.isTrainersUsePokemonOfSimilarStrength());
        tpComboBox.setSelectedItem(trainerSettings.get(settings.getTrainersMod().ordinal()));
        tpRivalCarriesStarterCheckBox.setSelected(settings.isRivalCarriesStarterThroughout());
        tpWeightTypesCheckBox.setSelected(settings.isTrainersMatchTypingDistribution());
        tpDontUseLegendariesCheckBox.setSelected(settings.isTrainersBlockLegendaries());
        tpNoEarlyWonderGuardCheckBox.setSelected(settings.isTrainersBlockEarlyWonderGuard());
        tpForceFullyEvolvedAtCheckBox.setSelected(settings.isTrainersForceFullyEvolved());
        tpForceFullyEvolvedAtSlider.setValue(settings.getTrainersForceFullyEvolvedLevel());
        tpPercentageLevelModifierCheckBox.setSelected(settings.isTrainersLevelModified());
        tpPercentageLevelModifierSlider.setValue(settings.getTrainersLevelModifier());
        tpEliteFourUniquePokemonCheckBox.setSelected(settings.getEliteFourUniquePokemonNumber() > 0);
        tpEliteFourUniquePokemonSpinner.setValue(settings.getEliteFourUniquePokemonNumber() > 0 ? settings.getEliteFourUniquePokemonNumber() : 1);
        tpAllowAlternateFormesCheckBox.setSelected(settings.isAllowTrainerAlternateFormes());
        tpSwapMegaEvosCheckBox.setSelected(settings.isSwapTrainerMegaEvos());
        tpDoubleBattleModeCheckBox.setSelected(settings.isDoubleBattleMode());
        tpBossTrainersCheckBox.setSelected(settings.getAdditionalBossTrainerPokemon() > 0);
        tpBossTrainersSpinner.setValue(settings.getAdditionalBossTrainerPokemon() > 0 ? settings.getAdditionalBossTrainerPokemon() : 1);
        tpImportantTrainersCheckBox.setSelected(settings.getAdditionalImportantTrainerPokemon() > 0);
        tpImportantTrainersSpinner.setValue(settings.getAdditionalImportantTrainerPokemon() > 0 ? settings.getAdditionalImportantTrainerPokemon() : 1);
        tpRegularTrainersCheckBox.setSelected(settings.getAdditionalRegularTrainerPokemon() > 0);
        tpRegularTrainersSpinner.setValue(settings.getAdditionalRegularTrainerPokemon() > 0 ? settings.getAdditionalRegularTrainerPokemon() : 1);
        tpBossTrainersItemsCheckBox.setSelected(settings.isRandomizeHeldItemsForBossTrainerPokemon());
        tpImportantTrainersItemsCheckBox.setSelected(settings.isRandomizeHeldItemsForImportantTrainerPokemon());
        tpRegularTrainersItemsCheckBox.setSelected(settings.isRandomizeHeldItemsForRegularTrainerPokemon());
        tpConsumableItemsOnlyCheckBox.setSelected(settings.isConsumableItemsOnlyForTrainers());
        tpScriptedHeldItemsCheckBox.setSelected(settings.isScriptedTrainerHeldItems());
        tpSensibleItemsCheckBox.setSelected(settings.isSensibleItemsOnlyForTrainers());
        tpHighestLevelGetsItemCheckBox.setSelected(settings.isHighestLevelGetsItemsForTrainers());

        tpRandomShinyTrainerPokemonCheckBox.setSelected(settings.isShinyChance());
        tpBetterMovesetsCheckBox.setSelected(settings.isBetterTrainerMovesets());

        totpUnchangedRadioButton.setSelected(settings.getTotemPokemonMod() == Settings.TotemPokemonMod.UNCHANGED);
        totpRandomRadioButton.setSelected(settings.getTotemPokemonMod() == Settings.TotemPokemonMod.RANDOM);
        totpRandomSimilarStrengthRadioButton.setSelected(settings.getTotemPokemonMod() == Settings.TotemPokemonMod.SIMILAR_STRENGTH);
        totpAllyUnchangedRadioButton.setSelected(settings.getAllyPokemonMod() == Settings.AllyPokemonMod.UNCHANGED);
        totpAllyRandomRadioButton.setSelected(settings.getAllyPokemonMod() == Settings.AllyPokemonMod.RANDOM);
        totpAllyRandomSimilarStrengthRadioButton.setSelected(settings.getAllyPokemonMod() == Settings.AllyPokemonMod.SIMILAR_STRENGTH);
        totpAuraUnchangedRadioButton.setSelected(settings.getAuraMod() == Settings.AuraMod.UNCHANGED);
        totpAuraRandomRadioButton.setSelected(settings.getAuraMod() == Settings.AuraMod.RANDOM);
        totpAuraRandomSameStrengthRadioButton.setSelected(settings.getAuraMod() == Settings.AuraMod.SAME_STRENGTH);
        totpRandomizeHeldItemsCheckBox.setSelected(settings.isRandomizeTotemHeldItems());
        totpAllowAltFormesCheckBox.setSelected(settings.isAllowTotemAltFormes());
        totpPercentageLevelModifierCheckBox.setSelected(settings.isTotemLevelsModified());
        totpPercentageLevelModifierSlider.setValue(settings.getTotemLevelModifier());

        wpARCatchEmAllModeRadioButton
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.CATCH_EM_ALL);
        wpArea1To1RadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.AREA_MAPPING);
        wpARNoneRadioButton.setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.NONE);
        wpARTypeThemeAreasRadioButton
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.TYPE_THEME_AREAS);
        wpScriptedRadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.SCRIPTED);
        wpGlobal1To1RadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.GLOBAL_MAPPING);
        wpRandomRadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.RANDOM);
        wpUnchangedRadioButton.setSelected(settings.getWildPokemonMod() == Settings.WildPokemonMod.UNCHANGED);
        wpUseTimeBasedEncountersCheckBox.setSelected(settings.isUseTimeBasedEncounters());

        wpSetMinimumCatchRateCheckBox.setSelected(settings.isUseMinimumCatchRate());
        wpSetMinimumCatchRateSlider.setValue(settings.getMinimumCatchRateLevel());
        wpDontUseLegendariesCheckBox.setSelected(settings.isBlockWildLegendaries());
        wpARSimilarStrengthRadioButton
                .setSelected(settings.getWildPokemonRestrictionMod() == Settings.WildPokemonRestrictionMod.SIMILAR_STRENGTH);
        wpRandomizeHeldItemsCheckBox.setSelected(settings.isRandomizeWildPokemonHeldItems());
        wpBanBadItemsCheckBox.setSelected(settings.isBanBadRandomWildPokemonHeldItems());
        wpScriptHeldItemsCheckBox.setSelected(settings.isScriptedWildHeldItems());
        wpBalanceShakingGrassPokemonCheckBox.setSelected(settings.isBalanceShakingGrass());
        wpPercentageLevelModifierCheckBox.setSelected(settings.isWildLevelsModified());
        wpPercentageLevelModifierSlider.setValue(settings.getWildLevelModifier());
        wpAllowAltFormesCheckBox.setSelected(settings.isAllowWildAltFormes());

        stpUnchangedRadioButton.setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.UNCHANGED);
        stpSwapLegendariesSwapStandardsRadioButton.setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.RANDOM_MATCHING);
        stpRandomCompletelyRadioButton
                .setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.COMPLETELY_RANDOM);
        stpRandomSimilarStrengthRadioButton
                .setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.SIMILAR_STRENGTH);
        stpScriptedRadioButton
                .setSelected(settings.getStaticPokemonMod() == Settings.StaticPokemonMod.SCRIPTED);
        stpLimitMainGameLegendariesCheckBox.setSelected(settings.isLimitMainGameLegendaries());
        stpRandomize600BSTCheckBox.setSelected(settings.isLimit600());
        stpAllowAltFormesCheckBox.setSelected(settings.isAllowStaticAltFormes());
        stpSwapMegaEvosCheckBox.setSelected(settings.isSwapStaticMegaEvos());
        stpPercentageLevelModifierCheckBox.setSelected(settings.isStaticLevelModified());
        stpPercentageLevelModifierSlider.setValue(settings.getStaticLevelModifier());
        stpFixMusicCheckBox.setSelected(settings.isCorrectStaticMusic());

        thcRandomCompletelyRadioButton
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.COMPLETELY_RANDOM);
        thcRandomPreferSameTypeRadioButton
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.RANDOM_PREFER_TYPE);
        thcScriptedRadioButton
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.SCRIPTED);
        thcUnchangedRadioButton
                .setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.UNCHANGED);
        tmRandomRadioButton.setSelected(settings.getTmsMod() == Settings.TMsMod.RANDOM);
        tmScriptedRadioButton.setSelected(settings.getTmsMod() == Settings.TMsMod.SCRIPTED);
        tmUnchangedRadioButton.setSelected(settings.getTmsMod() == Settings.TMsMod.UNCHANGED);
        tmLevelupMoveSanityCheckBox.setSelected(settings.isTmLevelUpMoveSanity());
        tmKeepFieldMoveTMsCheckBox.setSelected(settings.isKeepFieldMoveTMs());
        thcFullCompatibilityRadioButton.setSelected(settings.getTmsHmsCompatibilityMod() == Settings.TMsHMsCompatibilityMod.FULL);
        tmFullHMCompatibilityCheckBox.setSelected(settings.isFullHMCompat());
        tmForceGoodDamagingCheckBox.setSelected(settings.isTmsForceGoodDamaging());
        tmForceGoodDamagingSlider.setValue(settings.getTmsGoodDamagingPercent());
        tmNoGameBreakingMovesCheckBox.setSelected(settings.isBlockBrokenTMMoves());
        tmFollowEvolutionsCheckBox.setSelected(settings.isTmsFollowEvolutions());

        mtcRandomCompletelyRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.COMPLETELY_RANDOM);
        mtcRandomPreferSameTypeRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.RANDOM_PREFER_TYPE);
        mtcScriptedRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.SCRIPTED);
        mtcUnchangedRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.UNCHANGED);
        mtRandomRadioButton.setSelected(settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.RANDOM);
        mtScriptedRadioButton.setSelected(settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.SCRIPTED);
        mtUnchangedRadioButton.setSelected(settings.getMoveTutorMovesMod() == Settings.MoveTutorMovesMod.UNCHANGED);
        mtLevelupMoveSanityCheckBox.setSelected(settings.isTutorLevelUpMoveSanity());
        mtKeepFieldMoveTutorsCheckBox.setSelected(settings.isKeepFieldMoveTutors());
        mtcFullCompatibilityRadioButton
                .setSelected(settings.getMoveTutorsCompatibilityMod() == Settings.MoveTutorsCompatibilityMod.FULL);
        mtForceGoodDamagingCheckBox.setSelected(settings.isTutorsForceGoodDamaging());
        mtForceGoodDamagingSlider.setValue(settings.getTutorsGoodDamagingPercent());
        mtNoGameBreakingMovesCheckBox.setSelected(settings.isBlockBrokenTutorMoves());
        mtFollowEvolutionsCheckBox.setSelected(settings.isTutorFollowEvolutions());

        igtRandomizeBothRequestedGivenRadioButton
                .setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED);
        igtRandomizeGivenPokemonOnlyRadioButton.setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.RANDOMIZE_GIVEN);
        igtScriptedRadioButton.setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.SCRIPTED);
        igtRandomizeItemsCheckBox.setSelected(settings.isRandomizeInGameTradesItems());
        igtRandomizeIVsCheckBox.setSelected(settings.isRandomizeInGameTradesIVs());
        igtRandomizeNicknamesCheckBox.setSelected(settings.isRandomizeInGameTradesNicknames());
        igtRandomizeOTsCheckBox.setSelected(settings.isRandomizeInGameTradesOTs());
        igtUnchangedRadioButton.setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.UNCHANGED);
        igtScriptedRadioButton.setSelected(settings.getInGameTradesMod() == Settings.InGameTradesMod.SCRIPTED);

        fiRandomRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM);
        fiRandomEvenDistributionRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.RANDOM_EVEN);
        fiShuffleRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.SHUFFLE);
        fiScriptedRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.SCRIPTED);
        fiUnchangedRadioButton.setSelected(settings.getFieldItemsMod() == Settings.FieldItemsMod.UNCHANGED);
        fiBanBadItemsCheckBox.setSelected(settings.isBanBadRandomFieldItems());
        fiShuffleItemsCheckBox.setSelected(settings.isShuffleFieldItems());

        shRandomRadioButton.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.RANDOM);
        shShuffleRadioButton.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.SHUFFLE);
        shUnchangedRadioButton.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.UNCHANGED);
        shScriptedRadioButton.setSelected(settings.getShopItemsMod() == Settings.ShopItemsMod.SCRIPTED);
        shScriptedPricesCheckbox.setSelected(settings.isScriptedShopPrices());
        shBanBadItemsCheckBox.setSelected(settings.isBanBadRandomShopItems());
        shBanRegularShopItemsCheckBox.setSelected(settings.isBanRegularShopItems());
        shBanOverpoweredShopItemsCheckBox.setSelected(settings.isBanOPShopItems());
        shBalanceShopItemPricesCheckBox.setSelected(settings.isBalanceShopPrices());
        shGuaranteeEvolutionItemsCheckBox.setSelected(settings.isGuaranteeEvolutionItems());
        shGuaranteeXItemsCheckBox.setSelected(settings.isGuaranteeXItems());

        puUnchangedRadioButton.setSelected(settings.getPickupItemsMod() == Settings.PickupItemsMod.UNCHANGED);
        puRandomRadioButton.setSelected(settings.getPickupItemsMod() == Settings.PickupItemsMod.RANDOM);
        puScriptedRadioButton.setSelected(settings.getPickupItemsMod() == Settings.PickupItemsMod.SCRIPTED);
        puBanBadItemsCheckBox.setSelected(settings.isBanBadRandomPickupItems());

        sScriptInput.setText(settings.getScriptSource());

        int mtsSelected = settings.getCurrentMiscTweaks();
        int mtCount = MiscTweak.allTweaks.size();

        for (int mti = 0; mti < mtCount; mti++) {
            MiscTweak mt = MiscTweak.allTweaks.get(mti);
            JCheckBox mtCB = tweakCheckBoxes.get(mti);
            mtCB.setSelected((mtsSelected & mt.getValue()) != 0);
        }

        this.enableOrDisableSubControls();
    }

    private Settings createSettingsFromState(CustomNamesSet customNames) {
        Settings settings = new Settings();
        settings.setRomName(this.romHandler.getROMName());

        settings.setLimitPokemon(limitPokemonCheckBox.isSelected() && limitPokemonCheckBox.isVisible());
        settings.setScriptedPokemonLimit(limitPokemonScriptingCheckbox.isSelected());
        settings.setCurrentRestrictions(currentRestrictions);
        settings.setBanIrregularAltFormes(noIrregularAltFormesCheckBox.isSelected() && noIrregularAltFormesCheckBox.isVisible());
        settings.setRaceMode(raceModeCheckBox.isSelected());

        settings.setChangeImpossibleEvolutions(peChangeImpossibleEvosCheckBox.isSelected() && peChangeImpossibleEvosCheckBox.isVisible());
        settings.setUpdateMoves(mdUpdateMovesCheckBox.isSelected() && mdUpdateMovesCheckBox.isVisible());
        settings.setUpdateMovesToGeneration(mdUpdateComboBox.getSelectedIndex() + (romHandler.generationOfPokemon()+1));
        settings.setRandomizeTrainerNames(tpRandomizeTrainerNamesCheckBox.isSelected());
        settings.setRandomizeTrainerClassNames(tpRandomizeTrainerClassNamesCheckBox.isSelected());

        settings.setBaseStatisticsMod(pbsUnchangedRadioButton.isSelected(), pbsShuffleRadioButton.isSelected(),
                pbsRandomRadioButton.isSelected(), pbsScriptedRadioButton.isSelected());
        settings.setBaseStatsFollowEvolutions(pbsFollowEvolutionsCheckBox.isSelected());
        settings.setUpdateBaseStats(pbsUpdateBaseStatsCheckBox.isSelected() && pbsUpdateBaseStatsCheckBox.isVisible());
        settings.setUpdateBaseStatsToGeneration(pbsUpdateComboBox.getSelectedIndex() + (Math.max(6,romHandler.generationOfPokemon()+1)));
        settings.setStandardizeEXPCurves(pbsStandardizeEXPCurvesRadioButton.isSelected());
        settings.setScriptEXPCurves(pbsScriptedEXPCurveRadioButton.isSelected());
        settings.setExpCurveMod(pbsLegendariesSlowRadioButton.isSelected(), pbsStrongLegendariesSlowRadioButton.isSelected(),
                pbsAllMediumFastRadioButton.isSelected());
        ExpCurve[] expCurves = getEXPCurvesForGeneration(romHandler.generationOfPokemon());
        settings.setSelectedEXPCurve(expCurves[pbsEXPCurveComboBox.getSelectedIndex()]);
        settings.setBaseStatsFollowMegaEvolutions(pbsFollowMegaEvosCheckBox.isSelected() && pbsFollowMegaEvosCheckBox.isVisible());
        settings.setAssignEvoStatsRandomly(pbsAssignEvoStatsRandomlyCheckBox.isSelected() && pbsAssignEvoStatsRandomlyCheckBox.isVisible());

        settings.setAbilitiesMod(paUnchangedRadioButton.isSelected(), paRandomRadioButton.isSelected(), paScriptedRadioButton.isSelected());
        settings.setAllowWonderGuard(paAllowWonderGuardCheckBox.isSelected());
        settings.setAbilitiesFollowEvolutions(paFollowEvolutionsCheckBox.isSelected());
        settings.setBanTrappingAbilities(paTrappingAbilitiesCheckBox.isSelected());
        settings.setBanNegativeAbilities(paNegativeAbilitiesCheckBox.isSelected());
        settings.setBanBadAbilities(paBadAbilitiesCheckBox.isSelected());
        settings.setAbilitiesFollowMegaEvolutions(paFollowMegaEvosCheckBox.isSelected());
        settings.setWeighDuplicateAbilitiesTogether(paWeighDuplicatesTogetherCheckBox.isSelected());
        settings.setEnsureTwoAbilities(paEnsureTwoAbilitiesCheckbox.isSelected());

        settings.setTypesMod(ptUnchangedRadioButton.isSelected(), ptRandomFollowEvolutionsRadioButton.isSelected(),
                ptRandomCompletelyRadioButton.isSelected(), ptScriptedRadioButton.isSelected());
        settings.setTypesFollowMegaEvolutions(ptFollowMegaEvosCheckBox.isSelected() && ptFollowMegaEvosCheckBox.isVisible());
        settings.setBlockBrokenMovesetMoves(pmsNoGameBreakingMovesCheckBox.isSelected());
        settings.setDualTypeOnly(ptIsDualTypeCheckBox.isSelected());

        settings.setMakeEvolutionsEasier(peMakeEvolutionsEasierCheckBox.isSelected());
        settings.setRemoveTimeBasedEvolutions(peRemoveTimeBasedEvolutionsCheckBox.isSelected());

        settings.setStartersMod(spUnchangedRadioButton.isSelected(), spCustomRadioButton.isSelected(), spRandomCompletelyRadioButton.isSelected(),
                spRandomTwoEvosRadioButton.isSelected(), spScriptedRadioButton.isSelected());
        settings.setRandomizeStartersHeldItems(spRandomizeStarterHeldItemsRadioButton.isSelected() && spRandomizeStarterHeldItemsRadioButton.isVisible());
        settings.setScriptStarterHeldItems(spScriptedStarterHeldItemsRadioButton.isSelected() && spScriptedStarterHeldItemsRadioButton.isVisible());
        settings.setBanBadRandomStarterHeldItems(spBanBadItemsCheckBox.isSelected() && spBanBadItemsCheckBox.isVisible());
        settings.setAllowStarterAltFormes(spAllowAltFormesCheckBox.isSelected() && spAllowAltFormesCheckBox.isVisible());

        int[] customStarters = new int[] { spComboBox1.getSelectedIndex() + 1,
                spComboBox2.getSelectedIndex() + 1, spComboBox3.getSelectedIndex() + 1 };
        settings.setCustomStarters(customStarters);

        settings.setEvolutionsMod(peUnchangedRadioButton.isSelected(), peRandomRadioButton.isSelected(), peRandomEveryLevelRadioButton.isSelected(), peScriptedRadioButton.isSelected());
        settings.setEvosSimilarStrength(peSimilarStrengthCheckBox.isSelected());
        settings.setEvosSameTyping(peSameTypingCheckBox.isSelected());
        settings.setEvosMaxThreeStages(peLimitEvolutionsToThreeCheckBox.isSelected());
        settings.setEvosForceChange(peForceChangeCheckBox.isSelected());
        settings.setEvosAllowAltFormes(peAllowAltFormesCheckBox.isSelected() && peAllowAltFormesCheckBox.isVisible());

        settings.setRandomizeMoveAccuracies(mdRandomizeMoveAccuracyCheckBox.isSelected());
        settings.setRandomizeMoveCategory(mdRandomizeMoveCategoryCheckBox.isSelected());
        settings.setRandomizeMovePowers(mdRandomizeMovePowerCheckBox.isSelected());
        settings.setRandomizeMovePPs(mdRandomizeMovePPCheckBox.isSelected());
        settings.setRandomizeMoveTypes(mdRandomizeMoveTypesCheckBox.isSelected());
        settings.setScriptMoveData(mdScriptedCheckBox.isSelected());

        settings.setMovesetsMod(pmsUnchangedRadioButton.isSelected(), pmsRandomPreferringSameTypeRadioButton.isSelected(),
                pmsRandomCompletelyRadioButton.isSelected(), pmsMetronomeOnlyModeRadioButton.isSelected());
        settings.setStartWithGuaranteedMoves(pmsGuaranteedLevel1MovesCheckBox.isSelected() && pmsGuaranteedLevel1MovesCheckBox.isVisible());
        settings.setGuaranteedMoveCount(pmsGuaranteedLevel1MovesSlider.getValue());
        settings.setReorderDamagingMoves(pmsReorderDamagingMovesCheckBox.isSelected());
        settings.setScriptLearntMoves(pmsScriptLearntCheckBox.isSelected());
        settings.setScriptAfterLearntMoves(pmsScriptLearnAfterCheckBox.isSelected());
        settings.setScriptEggMoves(pmsScriptEggCheckBox.isSelected());

        settings.setMovesetsForceGoodDamaging(pmsForceGoodDamagingCheckBox.isSelected());
        settings.setMovesetsGoodDamagingPercent(pmsForceGoodDamagingSlider.getValue());
        settings.setBlockBrokenMovesetMoves(pmsNoGameBreakingMovesCheckBox.isSelected());
        settings.setEvolutionMovesForAll(pmsEvolutionMovesCheckBox.isVisible() &&
                pmsEvolutionMovesCheckBox.isSelected());

        settings.setTrainersMod(isTrainerSetting(TRAINER_UNCHANGED), isTrainerSetting(TRAINER_RANDOM),
                isTrainerSetting(TRAINER_RANDOM_EVEN), isTrainerSetting(TRAINER_RANDOM_EVEN_MAIN),
                isTrainerSetting(TRAINER_TYPE_THEMED), isTrainerSetting(TRAINER_TYPE_THEMED_ELITE4_GYMS),
                isTrainerSetting(TRAINER_SCRIPTED));
        settings.setTrainersUsePokemonOfSimilarStrength(tpSimilarStrengthCheckBox.isSelected());
        settings.setRivalCarriesStarterThroughout(tpRivalCarriesStarterCheckBox.isSelected());
        settings.setTrainersMatchTypingDistribution(tpWeightTypesCheckBox.isSelected());
        settings.setTrainersBlockLegendaries(tpDontUseLegendariesCheckBox.isSelected());
        settings.setTrainersBlockEarlyWonderGuard(tpNoEarlyWonderGuardCheckBox.isSelected());
        settings.setTrainersForceFullyEvolved(tpForceFullyEvolvedAtCheckBox.isSelected());
        settings.setTrainersForceFullyEvolvedLevel(tpForceFullyEvolvedAtSlider.getValue());
        settings.setTrainersLevelModified(tpPercentageLevelModifierCheckBox.isSelected());
        settings.setTrainersLevelModifier(tpPercentageLevelModifierSlider.getValue());
        settings.setEliteFourUniquePokemonNumber(tpEliteFourUniquePokemonCheckBox.isVisible() && tpEliteFourUniquePokemonCheckBox.isSelected() ? (int)tpEliteFourUniquePokemonSpinner.getValue() : 0);
        settings.setAllowTrainerAlternateFormes(tpAllowAlternateFormesCheckBox.isSelected() && tpAllowAlternateFormesCheckBox.isVisible());
        settings.setSwapTrainerMegaEvos(tpSwapMegaEvosCheckBox.isSelected() && tpSwapMegaEvosCheckBox.isVisible());
        settings.setDoubleBattleMode(tpDoubleBattleModeCheckBox.isVisible() && tpDoubleBattleModeCheckBox.isSelected());
        settings.setAdditionalBossTrainerPokemon(tpBossTrainersCheckBox.isVisible() && tpBossTrainersCheckBox.isSelected() ? (int)tpBossTrainersSpinner.getValue() : 0);
        settings.setAdditionalImportantTrainerPokemon(tpImportantTrainersCheckBox.isVisible() && tpImportantTrainersCheckBox.isSelected() ? (int)tpImportantTrainersSpinner.getValue() : 0);
        settings.setAdditionalRegularTrainerPokemon(tpRegularTrainersCheckBox.isVisible() && tpRegularTrainersCheckBox.isSelected() ? (int)tpRegularTrainersSpinner.getValue() : 0);
        settings.setShinyChance(tpRandomShinyTrainerPokemonCheckBox.isVisible() && tpRandomShinyTrainerPokemonCheckBox.isSelected());
        settings.setBetterTrainerMovesets(tpBetterMovesetsCheckBox.isVisible() && tpBetterMovesetsCheckBox.isSelected());
        settings.setRandomizeHeldItemsForBossTrainerPokemon(tpBossTrainersItemsCheckBox.isVisible() && tpBossTrainersItemsCheckBox.isSelected());
        settings.setRandomizeHeldItemsForImportantTrainerPokemon(tpImportantTrainersItemsCheckBox.isVisible() && tpImportantTrainersItemsCheckBox.isSelected());
        settings.setRandomizeHeldItemsForRegularTrainerPokemon(tpRegularTrainersItemsCheckBox.isVisible() && tpRegularTrainersItemsCheckBox.isSelected());
        settings.setConsumableItemsOnlyForTrainers(tpConsumableItemsOnlyCheckBox.isVisible() && tpConsumableItemsOnlyCheckBox.isSelected());
        settings.setScriptedTrainerHeldItems(tpScriptedHeldItemsCheckBox.isSelected());
        settings.setSensibleItemsOnlyForTrainers(tpSensibleItemsCheckBox.isVisible() && tpSensibleItemsCheckBox.isSelected());
        settings.setHighestLevelGetsItemsForTrainers(tpHighestLevelGetsItemCheckBox.isVisible() && tpHighestLevelGetsItemCheckBox.isSelected());

        settings.setTotemPokemonMod(totpUnchangedRadioButton.isSelected(), totpRandomRadioButton.isSelected(), totpRandomSimilarStrengthRadioButton.isSelected());
        settings.setAllyPokemonMod(totpAllyUnchangedRadioButton.isSelected(), totpAllyRandomRadioButton.isSelected(), totpAllyRandomSimilarStrengthRadioButton.isSelected());
        settings.setAuraMod(totpAuraUnchangedRadioButton.isSelected(), totpAuraRandomRadioButton.isSelected(), totpAuraRandomSameStrengthRadioButton.isSelected());
        settings.setRandomizeTotemHeldItems(totpRandomizeHeldItemsCheckBox.isSelected());
        settings.setAllowTotemAltFormes(totpAllowAltFormesCheckBox.isSelected());
        settings.setTotemLevelsModified(totpPercentageLevelModifierCheckBox.isSelected());
        settings.setTotemLevelModifier(totpPercentageLevelModifierSlider.getValue());

        settings.setWildPokemonMod(wpUnchangedRadioButton.isSelected(), wpRandomRadioButton.isSelected(), wpArea1To1RadioButton.isSelected(),
                wpGlobal1To1RadioButton.isSelected(), wpScriptedRadioButton.isSelected());
        settings.setWildPokemonRestrictionMod(wpARNoneRadioButton.isSelected(), wpARSimilarStrengthRadioButton.isSelected(),
                wpARCatchEmAllModeRadioButton.isSelected(), wpARTypeThemeAreasRadioButton.isSelected());
        settings.setUseTimeBasedEncounters(wpUseTimeBasedEncountersCheckBox.isSelected());
        settings.setUseMinimumCatchRate(wpSetMinimumCatchRateCheckBox.isSelected());
        settings.setMinimumCatchRateLevel(wpSetMinimumCatchRateSlider.getValue());
        settings.setBlockWildLegendaries(wpDontUseLegendariesCheckBox.isSelected());
        settings.setRandomizeWildPokemonHeldItems(wpRandomizeHeldItemsCheckBox.isSelected() && wpRandomizeHeldItemsCheckBox.isVisible());
        settings.setBanBadRandomWildPokemonHeldItems(wpBanBadItemsCheckBox.isSelected() && wpBanBadItemsCheckBox.isVisible());
        settings.setScriptedWildHeldItems(wpScriptHeldItemsCheckBox.isSelected() && wpScriptHeldItemsCheckBox.isVisible());
        settings.setBalanceShakingGrass(wpBalanceShakingGrassPokemonCheckBox.isSelected() && wpBalanceShakingGrassPokemonCheckBox.isVisible());
        settings.setWildLevelsModified(wpPercentageLevelModifierCheckBox.isSelected());
        settings.setWildLevelModifier(wpPercentageLevelModifierSlider.getValue());
        settings.setAllowWildAltFormes(wpAllowAltFormesCheckBox.isSelected() && wpAllowAltFormesCheckBox.isVisible());

        settings.setStaticPokemonMod(stpUnchangedRadioButton.isSelected(), stpSwapLegendariesSwapStandardsRadioButton.isSelected(),
                stpRandomCompletelyRadioButton.isSelected(), stpRandomSimilarStrengthRadioButton.isSelected(), stpScriptedRadioButton.isSelected());
        settings.setLimitMainGameLegendaries(stpLimitMainGameLegendariesCheckBox.isSelected() && stpLimitMainGameLegendariesCheckBox.isVisible());
        settings.setLimit600(stpRandomize600BSTCheckBox.isSelected());
        settings.setAllowStaticAltFormes(stpAllowAltFormesCheckBox.isSelected() && stpAllowAltFormesCheckBox.isVisible());
        settings.setSwapStaticMegaEvos(stpSwapMegaEvosCheckBox.isSelected() && stpSwapMegaEvosCheckBox.isVisible());
        settings.setStaticLevelModified(stpPercentageLevelModifierCheckBox.isSelected());
        settings.setStaticLevelModifier(stpPercentageLevelModifierSlider.getValue());
        settings.setCorrectStaticMusic(stpFixMusicCheckBox.isSelected() && stpFixMusicCheckBox.isVisible());

        settings.setTmsMod(tmUnchangedRadioButton.isSelected(), tmRandomRadioButton.isSelected(), tmScriptedRadioButton.isSelected());

        settings.setTmsHmsCompatibilityMod(thcUnchangedRadioButton.isSelected(), thcRandomPreferSameTypeRadioButton.isSelected(),
                thcRandomCompletelyRadioButton.isSelected(), thcFullCompatibilityRadioButton.isSelected(), thcScriptedRadioButton.isSelected());
        settings.setTmLevelUpMoveSanity(tmLevelupMoveSanityCheckBox.isSelected());
        settings.setKeepFieldMoveTMs(tmKeepFieldMoveTMsCheckBox.isSelected());
        settings.setFullHMCompat(tmFullHMCompatibilityCheckBox.isSelected() && tmFullHMCompatibilityCheckBox.isVisible());
        settings.setTmsForceGoodDamaging(tmForceGoodDamagingCheckBox.isSelected());
        settings.setTmsGoodDamagingPercent(tmForceGoodDamagingSlider.getValue());
        settings.setBlockBrokenTMMoves(tmNoGameBreakingMovesCheckBox.isSelected());
        settings.setTmsFollowEvolutions(tmFollowEvolutionsCheckBox.isSelected());

        settings.setMoveTutorMovesMod(mtUnchangedRadioButton.isSelected(), mtRandomRadioButton.isSelected(), mtScriptedRadioButton.isSelected());
        settings.setMoveTutorsCompatibilityMod(mtcUnchangedRadioButton.isSelected(), mtcRandomPreferSameTypeRadioButton.isSelected(),
                mtcRandomCompletelyRadioButton.isSelected(), mtcFullCompatibilityRadioButton.isSelected(), mtcScriptedRadioButton.isSelected());
        settings.setTutorLevelUpMoveSanity(mtLevelupMoveSanityCheckBox.isSelected());
        settings.setKeepFieldMoveTutors(mtKeepFieldMoveTutorsCheckBox.isSelected());
        settings.setTutorsForceGoodDamaging(mtForceGoodDamagingCheckBox.isSelected());
        settings.setTutorsGoodDamagingPercent(mtForceGoodDamagingSlider.getValue());
        settings.setBlockBrokenTutorMoves(mtNoGameBreakingMovesCheckBox.isSelected());
        settings.setTutorFollowEvolutions(mtFollowEvolutionsCheckBox.isSelected());

        settings.setInGameTradesMod(igtUnchangedRadioButton.isSelected(), igtRandomizeGivenPokemonOnlyRadioButton.isSelected(), igtRandomizeBothRequestedGivenRadioButton.isSelected(), igtScriptedRadioButton.isSelected());
        settings.setRandomizeInGameTradesItems(igtRandomizeItemsCheckBox.isSelected());
        settings.setRandomizeInGameTradesIVs(igtRandomizeIVsCheckBox.isSelected());
        settings.setRandomizeInGameTradesNicknames(igtRandomizeNicknamesCheckBox.isSelected());
        settings.setRandomizeInGameTradesOTs(igtRandomizeOTsCheckBox.isSelected());

        settings.setFieldItemsMod(fiUnchangedRadioButton.isSelected(), fiShuffleRadioButton.isSelected(), fiRandomRadioButton.isSelected(), fiRandomEvenDistributionRadioButton.isSelected(), fiScriptedRadioButton.isSelected());
        settings.setBanBadRandomFieldItems(fiBanBadItemsCheckBox.isSelected());
        settings.setShuffleFieldItems(fiShuffleItemsCheckBox.isSelected());

        settings.setShopItemsMod(shUnchangedRadioButton.isSelected(), shShuffleRadioButton.isSelected(), shRandomRadioButton.isSelected(), shScriptedRadioButton.isSelected());
        settings.setBanBadRandomShopItems(shBanBadItemsCheckBox.isSelected());
        settings.setBanRegularShopItems(shBanRegularShopItemsCheckBox.isSelected());
        settings.setBanOPShopItems(shBanOverpoweredShopItemsCheckBox.isSelected());
        settings.setBalanceShopPrices(shBalanceShopItemPricesCheckBox.isSelected());
        settings.setGuaranteeEvolutionItems(shGuaranteeEvolutionItemsCheckBox.isSelected());
        settings.setGuaranteeXItems(shGuaranteeXItemsCheckBox.isSelected());
        settings.setScriptedShopPrices(shScriptedPricesCheckbox.isSelected());

        settings.setPickupItemsMod(puUnchangedRadioButton.isSelected(), puRandomRadioButton.isSelected(), puScriptedRadioButton.isSelected());
        settings.setBanBadRandomPickupItems(puBanBadItemsCheckBox.isSelected());

        int currentMiscTweaks = 0;
        int mtCount = MiscTweak.allTweaks.size();

        for (int mti = 0; mti < mtCount; mti++) {
            MiscTweak mt = MiscTweak.allTweaks.get(mti);
            JCheckBox mtCB = tweakCheckBoxes.get(mti);
            if (mtCB.isSelected()) {
                currentMiscTweaks |= mt.getValue();
            }
        }

        settings.setCurrentMiscTweaks(currentMiscTweaks);

        settings.setCustomNames(customNames);

        settings.setScriptSource(sScriptInput.getText());

        return settings;
    }

    private Settings getCurrentSettings() throws IOException {
        return createSettingsFromState(FileFunctions.getCustomNames());
    }

    private void attemptToLogException(Exception ex, String baseMessageKey, String noLogMessageKey,
                                       String settingsString, String seedString) {
        attemptToLogException(ex, baseMessageKey, noLogMessageKey, false, settingsString, seedString);
    }

    private void attemptToLogException(Exception ex, String baseMessageKey, String noLogMessageKey, boolean showMessage,
                                       String settingsString, String seedString) {

        // Make sure the operation dialog doesn't show up over the error
        // dialog
        SwingUtilities.invokeLater(() -> NewRandomizerGUI.this.opDialog.setVisible(false));

        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
            String errlog = "error_" + ft.format(now) + ".txt";
            PrintStream ps = new PrintStream(new FileOutputStream(errlog));
            ps.println("Randomizer Version: " + Version.VERSION_STRING);
            if (seedString != null) {
                ps.println("Seed: " + seedString);
            }
            if (settingsString != null) {
                ps.println("Settings String: " + Version.VERSION + settingsString);
            }
            ps.println("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vm.name"));

            PrintStream e1 = System.err;
            ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
            PrintStream tempPrint = new PrintStream(tempStream);
            System.setErr(tempPrint);

            if (this.romHandler != null) {
                try {
                    tempPrint.println("ROM: " + romHandler.getROMName());
                    tempPrint.println("Code: " + romHandler.getROMCode());
                    tempPrint.println("Reported Support Level: " + romHandler.getSupportLevel());
                    tempPrint.println();
                } catch (Exception ex2) {
                    // Do nothing, just don't fail
                }
            }
            ex.printStackTrace();
            tempPrint.println();
            tempPrint.println("--ROM Diagnostics--");
            if (!romHandler.isRomValid()) {
                tempPrint.println(bundle.getString("Log.InvalidRomLoaded"));
            }
            romHandler.printRomDiagnostics(ps);
            System.setErr(ps);
            String printed = tempStream.toString();
            ps.print(printed);
            System.setErr(e1);
            e1.print(printed);
            ps.close();
            if (showMessage) {
                JOptionPane.showMessageDialog(mainPanel,
                        String.format(bundle.getString(baseMessageKey), ex.getMessage(), errlog));
            } else {
                JOptionPane.showMessageDialog(mainPanel, String.format(bundle.getString(baseMessageKey), errlog));
            }
        } catch (Exception logex) {
            if (showMessage) {
                JOptionPane.showMessageDialog(mainPanel, String.format(bundle.getString(noLogMessageKey), ex.getMessage()));
            } else {
                JOptionPane.showMessageDialog(mainPanel, bundle.getString(noLogMessageKey));
            }
        }
    }

    public String getValidRequiredROMName(String config, CustomNamesSet customNames)
            throws UnsupportedEncodingException, InvalidSupplementFilesException {
        try {
            Utils.validatePresetSupplementFiles(config, customNames);
        } catch (InvalidSupplementFilesException e) {
            switch (e.getType()) {
                case CUSTOM_NAMES:
                    JOptionPane.showMessageDialog(null, bundle.getString("GUI.presetDifferentCustomNames"));
                    break;
                default:
                    throw e;
            }
        }
        byte[] data = Base64.getDecoder().decode(config);

        int nameLength = data[Settings.LENGTH_OF_SETTINGS_DATA] & 0xFF; //ADD LENGTH OF SCRIPT AND SUCH TO THE CHECK BELOW!!!
        int srcLenBase = Settings.LENGTH_OF_SETTINGS_DATA + 1 + nameLength;
        int scriptSrcLength = Ints.fromBytes(data[srcLenBase + 0], data[srcLenBase + 1], data[srcLenBase + 2], data[srcLenBase + 3]);
        if (data.length != Settings.LENGTH_OF_SETTINGS_DATA + 9 + nameLength + 4 + scriptSrcLength) {
            return null; // not valid length
        }
        return new String(data, Settings.LENGTH_OF_SETTINGS_DATA + 1, nameLength, "US-ASCII");
    }

    private void initialState() {

        romNameLabel.setText(bundle.getString("GUI.noRomLoaded"));
        romCodeLabel.setText("");
        romSupportLabel.setText("");

        gameMascotLabel.setIcon(emptyIcon);

        limitPokemonCheckBox.setVisible(true);
        limitPokemonCheckBox.setEnabled(false);
        limitPokemonCheckBox.setSelected(false);
        limitPokemonScriptingCheckbox.setVisible(true);
        limitPokemonScriptingCheckbox.setEnabled(false);
        limitPokemonScriptingCheckbox.setSelected(false);
        limitPokemonButton.setVisible(true);
        limitPokemonButton.setEnabled(false);
        noIrregularAltFormesCheckBox.setVisible(true);
        noIrregularAltFormesCheckBox.setEnabled(false);
        noIrregularAltFormesCheckBox.setSelected(false);
        raceModeCheckBox.setVisible(true);
        raceModeCheckBox.setEnabled(false);
        raceModeCheckBox.setSelected(false);

        currentRestrictions = null;

        openROMButton.setVisible(true);
        openROMButton.setEnabled(true);
        openROMButton.setSelected(false);
        randomizeSaveButton.setVisible(true);
        randomizeSaveButton.setEnabled(true);
        randomizeSaveButton.setSelected(false);
        premadeSeedButton.setVisible(true);
        premadeSeedButton.setEnabled(true);
        premadeSeedButton.setSelected(false);
        settingsButton.setVisible(true);
        settingsButton.setEnabled(true);
        settingsButton.setSelected(false);

        loadSettingsButton.setVisible(true);
        loadSettingsButton.setEnabled(false);
        loadSettingsButton.setSelected(false);
        saveSettingsButton.setVisible(true);
        saveSettingsButton.setEnabled(false);
        saveSettingsButton.setSelected(false);
        pbsUnchangedRadioButton.setVisible(true);
        pbsUnchangedRadioButton.setEnabled(false);
        pbsUnchangedRadioButton.setSelected(false);
        pbsShuffleRadioButton.setVisible(true);
        pbsShuffleRadioButton.setEnabled(false);
        pbsShuffleRadioButton.setSelected(false);
        pbsRandomRadioButton.setVisible(true);
        pbsRandomRadioButton.setEnabled(false);
        pbsRandomRadioButton.setSelected(false);
        pbsScriptedRadioButton.setVisible(true);
        pbsScriptedRadioButton.setEnabled(false);
        pbsScriptedRadioButton.setSelected(false);
        pbsLegendariesSlowRadioButton.setVisible(true);
        pbsLegendariesSlowRadioButton.setEnabled(false);
        pbsLegendariesSlowRadioButton.setSelected(false);
        pbsStrongLegendariesSlowRadioButton.setVisible(true);
        pbsStrongLegendariesSlowRadioButton.setEnabled(false);
        pbsStrongLegendariesSlowRadioButton.setSelected(false);
        pbsAllMediumFastRadioButton.setVisible(true);
        pbsAllMediumFastRadioButton.setEnabled(false);
        pbsAllMediumFastRadioButton.setSelected(false);
        pbsUnchangedEXPCurveRadioButton.setVisible(true);
        pbsUnchangedEXPCurveRadioButton.setEnabled(false);
        pbsUnchangedEXPCurveRadioButton.setSelected(false);
        pbsScriptedEXPCurveRadioButton.setVisible(true);
        pbsScriptedEXPCurveRadioButton.setEnabled(false);
        pbsScriptedEXPCurveRadioButton.setSelected(false);
        pbsStandardizeEXPCurvesRadioButton.setVisible(true);
        pbsStandardizeEXPCurvesRadioButton.setEnabled(false);
        pbsStandardizeEXPCurvesRadioButton.setSelected(false);
        pbsEXPCurveComboBox.setVisible(true);
        pbsEXPCurveComboBox.setEnabled(false);
        pbsEXPCurveComboBox.setSelectedIndex(0);
        pbsEXPCurveComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Medium Fast" }));
        pbsFollowEvolutionsCheckBox.setVisible(true);
        pbsFollowEvolutionsCheckBox.setEnabled(false);
        pbsFollowEvolutionsCheckBox.setSelected(false);
        pbsUpdateBaseStatsCheckBox.setVisible(true);
        pbsUpdateBaseStatsCheckBox.setEnabled(false);
        pbsUpdateBaseStatsCheckBox.setSelected(false);
        pbsFollowMegaEvosCheckBox.setVisible(true);
        pbsFollowMegaEvosCheckBox.setEnabled(false);
        pbsFollowMegaEvosCheckBox.setSelected(false);
        pbsUpdateComboBox.setVisible(true);
        pbsUpdateComboBox.setEnabled(false);
        pbsUpdateComboBox.setSelectedIndex(0);
        pbsUpdateComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        pbsAssignEvoStatsRandomlyCheckBox.setVisible(true);
        pbsAssignEvoStatsRandomlyCheckBox.setEnabled(false);
        pbsAssignEvoStatsRandomlyCheckBox.setSelected(false);
        ptUnchangedRadioButton.setVisible(true);
        ptUnchangedRadioButton.setEnabled(false);
        ptUnchangedRadioButton.setSelected(false);
        ptRandomFollowEvolutionsRadioButton.setVisible(true);
        ptRandomFollowEvolutionsRadioButton.setEnabled(false);
        ptRandomFollowEvolutionsRadioButton.setSelected(false);
        ptRandomCompletelyRadioButton.setVisible(true);
        ptRandomCompletelyRadioButton.setEnabled(false);
        ptRandomCompletelyRadioButton.setSelected(false);
        ptFollowMegaEvosCheckBox.setVisible(true);
        ptFollowMegaEvosCheckBox.setEnabled(false);
        ptFollowMegaEvosCheckBox.setSelected(false);
        ptScriptedRadioButton.setVisible(true);
        ptScriptedRadioButton.setEnabled(false);
        ptScriptedRadioButton.setSelected(false);
        ptIsDualTypeCheckBox.setVisible(true);
        ptIsDualTypeCheckBox.setEnabled(false);
        ptIsDualTypeCheckBox.setSelected(false);
        pokemonAbilitiesPanel.setVisible(true);
        paUnchangedRadioButton.setVisible(true);
        paUnchangedRadioButton.setEnabled(false);
        paUnchangedRadioButton.setSelected(false);
        paRandomRadioButton.setVisible(true);
        paRandomRadioButton.setEnabled(false);
        paRandomRadioButton.setSelected(false);
        paScriptedRadioButton.setVisible(true);
        paScriptedRadioButton.setEnabled(false);
        paScriptedRadioButton.setSelected(false);
        paAllowWonderGuardCheckBox.setVisible(true);
        paAllowWonderGuardCheckBox.setEnabled(false);
        paAllowWonderGuardCheckBox.setSelected(false);
        paFollowEvolutionsCheckBox.setVisible(true);
        paFollowEvolutionsCheckBox.setEnabled(false);
        paFollowEvolutionsCheckBox.setSelected(false);
        paTrappingAbilitiesCheckBox.setVisible(true);
        paTrappingAbilitiesCheckBox.setEnabled(false);
        paTrappingAbilitiesCheckBox.setSelected(false);
        paNegativeAbilitiesCheckBox.setVisible(true);
        paNegativeAbilitiesCheckBox.setEnabled(false);
        paNegativeAbilitiesCheckBox.setSelected(false);
        paBadAbilitiesCheckBox.setVisible(true);
        paBadAbilitiesCheckBox.setEnabled(false);
        paBadAbilitiesCheckBox.setSelected(false);
        paFollowMegaEvosCheckBox.setVisible(true);
        paFollowMegaEvosCheckBox.setEnabled(false);
        paFollowMegaEvosCheckBox.setSelected(false);
        paWeighDuplicatesTogetherCheckBox.setVisible(true);
        paWeighDuplicatesTogetherCheckBox.setEnabled(false);
        paWeighDuplicatesTogetherCheckBox.setSelected(false);
        paEnsureTwoAbilitiesCheckbox.setVisible(true);
        paEnsureTwoAbilitiesCheckbox.setEnabled(false);
        paEnsureTwoAbilitiesCheckbox.setSelected(false);
        peUnchangedRadioButton.setVisible(true);
        peUnchangedRadioButton.setEnabled(false);
        peUnchangedRadioButton.setSelected(false);
        peRandomRadioButton.setVisible(true);
        peRandomRadioButton.setEnabled(false);
        peRandomRadioButton.setSelected(false);
        peRandomEveryLevelRadioButton.setVisible(true);
        peRandomEveryLevelRadioButton.setEnabled(false);
        peRandomEveryLevelRadioButton.setSelected(false);
        peScriptedRadioButton.setVisible(true);
        peScriptedRadioButton.setEnabled(false);
        peScriptedRadioButton.setSelected(false);
        peSimilarStrengthCheckBox.setVisible(true);
        peSimilarStrengthCheckBox.setEnabled(false);
        peSimilarStrengthCheckBox.setSelected(false);
        peSameTypingCheckBox.setVisible(true);
        peSameTypingCheckBox.setEnabled(false);
        peSameTypingCheckBox.setSelected(false);
        peLimitEvolutionsToThreeCheckBox.setVisible(true);
        peLimitEvolutionsToThreeCheckBox.setEnabled(false);
        peLimitEvolutionsToThreeCheckBox.setSelected(false);
        peForceChangeCheckBox.setVisible(true);
        peForceChangeCheckBox.setEnabled(false);
        peForceChangeCheckBox.setSelected(false);
        peChangeImpossibleEvosCheckBox.setVisible(true);
        peChangeImpossibleEvosCheckBox.setEnabled(false);
        peChangeImpossibleEvosCheckBox.setSelected(false);
        peMakeEvolutionsEasierCheckBox.setVisible(true);
        peMakeEvolutionsEasierCheckBox.setEnabled(false);
        peMakeEvolutionsEasierCheckBox.setSelected(false);
        peRemoveTimeBasedEvolutionsCheckBox.setVisible(true);
        peRemoveTimeBasedEvolutionsCheckBox.setEnabled(false);
        peRemoveTimeBasedEvolutionsCheckBox.setSelected(false);
        peAllowAltFormesCheckBox.setVisible(true);
        peAllowAltFormesCheckBox.setEnabled(false);
        peAllowAltFormesCheckBox.setSelected(false);
        spUnchangedRadioButton.setVisible(true);
        spUnchangedRadioButton.setEnabled(false);
        spUnchangedRadioButton.setSelected(false);
        spCustomRadioButton.setVisible(true);
        spCustomRadioButton.setEnabled(false);
        spCustomRadioButton.setSelected(false);
        spRandomCompletelyRadioButton.setVisible(true);
        spRandomCompletelyRadioButton.setEnabled(false);
        spRandomCompletelyRadioButton.setSelected(false);
        spRandomTwoEvosRadioButton.setVisible(true);
        spRandomTwoEvosRadioButton.setEnabled(false);
        spRandomTwoEvosRadioButton.setSelected(false);
        spScriptedRadioButton.setVisible(true);
        spScriptedRadioButton.setEnabled(false);
        spScriptedRadioButton.setSelected(false);
        spComboBox1.setVisible(true);
        spComboBox1.setEnabled(false);
        spComboBox1.setSelectedIndex(0);
        spComboBox1.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        spComboBox2.setVisible(true);
        spComboBox2.setEnabled(false);
        spComboBox2.setSelectedIndex(0);
        spComboBox2.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        spComboBox3.setVisible(true);
        spComboBox3.setEnabled(false);
        spComboBox3.setSelectedIndex(0);
        spComboBox3.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        spUnchangedStarterHeldItemsRadioButton.setVisible(true);
        spUnchangedStarterHeldItemsRadioButton.setEnabled(false);
        spUnchangedStarterHeldItemsRadioButton.setSelected(true);
        spRandomizeStarterHeldItemsRadioButton.setVisible(true);
        spRandomizeStarterHeldItemsRadioButton.setEnabled(false);
        spRandomizeStarterHeldItemsRadioButton.setSelected(false);
        spScriptedStarterHeldItemsRadioButton.setVisible(true);
        spScriptedStarterHeldItemsRadioButton.setEnabled(false);
        spScriptedStarterHeldItemsRadioButton.setSelected(false);
        spBanBadItemsCheckBox.setVisible(true);
        spBanBadItemsCheckBox.setEnabled(false);
        spBanBadItemsCheckBox.setSelected(false);
        spAllowAltFormesCheckBox.setVisible(true);
        spAllowAltFormesCheckBox.setEnabled(false);
        spAllowAltFormesCheckBox.setSelected(false);
        stpUnchangedRadioButton.setVisible(true);
        stpUnchangedRadioButton.setEnabled(false);
        stpUnchangedRadioButton.setSelected(false);
        stpSwapLegendariesSwapStandardsRadioButton.setVisible(true);
        stpSwapLegendariesSwapStandardsRadioButton.setEnabled(false);
        stpSwapLegendariesSwapStandardsRadioButton.setSelected(false);
        stpRandomCompletelyRadioButton.setVisible(true);
        stpRandomCompletelyRadioButton.setEnabled(false);
        stpRandomCompletelyRadioButton.setSelected(false);
        stpRandomSimilarStrengthRadioButton.setVisible(true);
        stpRandomSimilarStrengthRadioButton.setEnabled(false);
        stpRandomSimilarStrengthRadioButton.setSelected(false);
        stpScriptedRadioButton.setVisible(true);
        stpScriptedRadioButton.setEnabled(false);
        stpScriptedRadioButton.setSelected(false);
        stpPercentageLevelModifierCheckBox.setVisible(true);
        stpPercentageLevelModifierCheckBox.setEnabled(false);
        stpPercentageLevelModifierCheckBox.setSelected(false);
        stpPercentageLevelModifierSlider.setVisible(true);
        stpPercentageLevelModifierSlider.setEnabled(false);
        stpPercentageLevelModifierSlider.setValue(0);
        stpLimitMainGameLegendariesCheckBox.setVisible(true);
        stpLimitMainGameLegendariesCheckBox.setEnabled(false);
        stpLimitMainGameLegendariesCheckBox.setSelected(false);
        stpRandomize600BSTCheckBox.setVisible(true);
        stpRandomize600BSTCheckBox.setEnabled(false);
        stpRandomize600BSTCheckBox.setSelected(false);
        stpAllowAltFormesCheckBox.setVisible(true);
        stpAllowAltFormesCheckBox.setEnabled(false);
        stpAllowAltFormesCheckBox.setSelected(false);
        stpSwapMegaEvosCheckBox.setVisible(true);
        stpSwapMegaEvosCheckBox.setEnabled(false);
        stpSwapMegaEvosCheckBox.setSelected(false);
        stpFixMusicCheckBox.setVisible(true);
        stpFixMusicCheckBox.setEnabled(false);
        stpFixMusicCheckBox.setSelected(false);
        igtUnchangedRadioButton.setVisible(true);
        igtUnchangedRadioButton.setEnabled(false);
        igtUnchangedRadioButton.setSelected(false);
        igtRandomizeGivenPokemonOnlyRadioButton.setVisible(true);
        igtRandomizeGivenPokemonOnlyRadioButton.setEnabled(false);
        igtRandomizeGivenPokemonOnlyRadioButton.setSelected(false);
        igtRandomizeBothRequestedGivenRadioButton.setVisible(true);
        igtRandomizeBothRequestedGivenRadioButton.setEnabled(false);
        igtRandomizeBothRequestedGivenRadioButton.setSelected(false);
        igtScriptedRadioButton.setVisible(true);
        igtScriptedRadioButton.setEnabled(false);
        igtScriptedRadioButton.setSelected(false);
        igtRandomizeNicknamesCheckBox.setVisible(true);
        igtRandomizeNicknamesCheckBox.setEnabled(false);
        igtRandomizeNicknamesCheckBox.setSelected(false);
        igtRandomizeOTsCheckBox.setVisible(true);
        igtRandomizeOTsCheckBox.setEnabled(false);
        igtRandomizeOTsCheckBox.setSelected(false);
        igtRandomizeIVsCheckBox.setVisible(true);
        igtRandomizeIVsCheckBox.setEnabled(false);
        igtRandomizeIVsCheckBox.setSelected(false);
        igtRandomizeItemsCheckBox.setVisible(true);
        igtRandomizeItemsCheckBox.setEnabled(false);
        igtRandomizeItemsCheckBox.setSelected(false);
        mdRandomizeMovePowerCheckBox.setVisible(true);
        mdRandomizeMovePowerCheckBox.setEnabled(false);
        mdRandomizeMovePowerCheckBox.setSelected(false);
        mdRandomizeMoveAccuracyCheckBox.setVisible(true);
        mdRandomizeMoveAccuracyCheckBox.setEnabled(false);
        mdRandomizeMoveAccuracyCheckBox.setSelected(false);
        mdRandomizeMovePPCheckBox.setVisible(true);
        mdRandomizeMovePPCheckBox.setEnabled(false);
        mdRandomizeMovePPCheckBox.setSelected(false);
        mdRandomizeMoveTypesCheckBox.setVisible(true);
        mdRandomizeMoveTypesCheckBox.setEnabled(false);
        mdRandomizeMoveTypesCheckBox.setSelected(false);
        mdRandomizeMoveCategoryCheckBox.setVisible(true);
        mdRandomizeMoveCategoryCheckBox.setEnabled(false);
        mdRandomizeMoveCategoryCheckBox.setSelected(false);
        mdScriptedCheckBox.setVisible(true);
        mdScriptedCheckBox.setEnabled(false);
        mdScriptedCheckBox.setSelected(false);
        mdUpdateMovesCheckBox.setVisible(true);
        mdUpdateMovesCheckBox.setEnabled(false);
        mdUpdateMovesCheckBox.setSelected(false);
        mdUpdateComboBox.setVisible(true);
        mdUpdateComboBox.setEnabled(false);
        mdUpdateComboBox.setSelectedIndex(0);
        mdUpdateComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "--" }));
        pmsUnchangedRadioButton.setVisible(true);
        pmsUnchangedRadioButton.setEnabled(false);
        pmsUnchangedRadioButton.setSelected(false);
        pmsRandomPreferringSameTypeRadioButton.setVisible(true);
        pmsRandomPreferringSameTypeRadioButton.setEnabled(false);
        pmsRandomPreferringSameTypeRadioButton.setSelected(false);
        pmsRandomCompletelyRadioButton.setVisible(true);
        pmsRandomCompletelyRadioButton.setEnabled(false);
        pmsRandomCompletelyRadioButton.setSelected(false);
        pmsMetronomeOnlyModeRadioButton.setVisible(true);
        pmsMetronomeOnlyModeRadioButton.setEnabled(false);
        pmsMetronomeOnlyModeRadioButton.setSelected(false);
        pmsGuaranteedLevel1MovesCheckBox.setVisible(true);
        pmsGuaranteedLevel1MovesCheckBox.setEnabled(false);
        pmsGuaranteedLevel1MovesCheckBox.setSelected(false);
        pmsReorderDamagingMovesCheckBox.setVisible(true);
        pmsReorderDamagingMovesCheckBox.setEnabled(false);
        pmsReorderDamagingMovesCheckBox.setSelected(false);
        pmsNoGameBreakingMovesCheckBox.setVisible(true);
        pmsNoGameBreakingMovesCheckBox.setEnabled(false);
        pmsNoGameBreakingMovesCheckBox.setSelected(false);
        pmsForceGoodDamagingCheckBox.setVisible(true);
        pmsForceGoodDamagingCheckBox.setEnabled(false);
        pmsForceGoodDamagingCheckBox.setSelected(false);
        pmsScriptLearntCheckBox.setVisible(true);
        pmsScriptLearntCheckBox.setEnabled(false);
        pmsScriptLearntCheckBox.setSelected(false);
        pmsScriptEggCheckBox.setVisible(true);
        pmsScriptEggCheckBox.setEnabled(false);
        pmsScriptEggCheckBox.setSelected(false);
        pmsScriptLearnAfterCheckBox.setVisible(true);
        pmsScriptLearnAfterCheckBox.setEnabled(false);
        pmsScriptLearnAfterCheckBox.setSelected(false);
        pmsGuaranteedLevel1MovesSlider.setVisible(true);
        pmsGuaranteedLevel1MovesSlider.setEnabled(false);
        pmsGuaranteedLevel1MovesSlider.setValue(pmsGuaranteedLevel1MovesSlider.getMinimum());
        pmsForceGoodDamagingSlider.setVisible(true);
        pmsForceGoodDamagingSlider.setEnabled(false);
        pmsForceGoodDamagingSlider.setValue(pmsForceGoodDamagingSlider.getMinimum());
        pmsEvolutionMovesCheckBox.setVisible(true);
        pmsEvolutionMovesCheckBox.setEnabled(false);
        pmsEvolutionMovesCheckBox.setSelected(false);
        tpComboBox.setVisible(true);
        tpComboBox.setEnabled(false);
        tpComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Unchanged" }));
        tpRivalCarriesStarterCheckBox.setVisible(true);
        tpRivalCarriesStarterCheckBox.setEnabled(false);
        tpRivalCarriesStarterCheckBox.setSelected(false);
        tpSimilarStrengthCheckBox.setVisible(true);
        tpSimilarStrengthCheckBox.setEnabled(false);
        tpSimilarStrengthCheckBox.setSelected(false);
        tpWeightTypesCheckBox.setVisible(true);
        tpWeightTypesCheckBox.setEnabled(false);
        tpWeightTypesCheckBox.setSelected(false);
        tpDontUseLegendariesCheckBox.setVisible(true);
        tpDontUseLegendariesCheckBox.setEnabled(false);
        tpDontUseLegendariesCheckBox.setSelected(false);
        tpNoEarlyWonderGuardCheckBox.setVisible(true);
        tpNoEarlyWonderGuardCheckBox.setEnabled(false);
        tpNoEarlyWonderGuardCheckBox.setSelected(false);
        tpRandomizeTrainerNamesCheckBox.setVisible(true);
        tpRandomizeTrainerNamesCheckBox.setEnabled(false);
        tpRandomizeTrainerNamesCheckBox.setSelected(false);
        tpRandomizeTrainerClassNamesCheckBox.setVisible(true);
        tpRandomizeTrainerClassNamesCheckBox.setEnabled(false);
        tpRandomizeTrainerClassNamesCheckBox.setSelected(false);
        tpForceFullyEvolvedAtCheckBox.setVisible(true);
        tpForceFullyEvolvedAtCheckBox.setEnabled(false);
        tpForceFullyEvolvedAtCheckBox.setSelected(false);
        tpForceFullyEvolvedAtSlider.setVisible(true);
        tpForceFullyEvolvedAtSlider.setEnabled(false);
        tpForceFullyEvolvedAtSlider.setValue(tpForceFullyEvolvedAtSlider.getMinimum());
        tpPercentageLevelModifierSlider.setVisible(true);
        tpPercentageLevelModifierSlider.setEnabled(false);
        tpPercentageLevelModifierSlider.setValue(0);
        tpPercentageLevelModifierCheckBox.setVisible(true);
        tpPercentageLevelModifierCheckBox.setEnabled(false);
        tpPercentageLevelModifierCheckBox.setSelected(false);

        tpEliteFourUniquePokemonCheckBox.setVisible(true);
        tpEliteFourUniquePokemonCheckBox.setEnabled(false);
        tpEliteFourUniquePokemonCheckBox.setSelected(false);
        tpEliteFourUniquePokemonSpinner.setVisible(true);
        tpEliteFourUniquePokemonSpinner.setEnabled(false);
        tpEliteFourUniquePokemonSpinner.setValue(1);

        tpAllowAlternateFormesCheckBox.setVisible(true);
        tpAllowAlternateFormesCheckBox.setEnabled(false);
        tpAllowAlternateFormesCheckBox.setSelected(false);
        tpSwapMegaEvosCheckBox.setVisible(true);
        tpSwapMegaEvosCheckBox.setEnabled(false);
        tpSwapMegaEvosCheckBox.setSelected(false);
        tpDoubleBattleModeCheckBox.setVisible(true);
        tpDoubleBattleModeCheckBox.setEnabled(false);
        tpDoubleBattleModeCheckBox.setSelected(false);
        tpBossTrainersCheckBox.setVisible(true);
        tpBossTrainersCheckBox.setEnabled(false);
        tpBossTrainersCheckBox.setSelected(false);
        tpImportantTrainersCheckBox.setVisible(true);
        tpImportantTrainersCheckBox.setEnabled(false);
        tpImportantTrainersCheckBox.setSelected(false);
        tpRegularTrainersCheckBox.setVisible(true);
        tpRegularTrainersCheckBox.setEnabled(false);
        tpRegularTrainersCheckBox.setSelected(false);
        tpBossTrainersSpinner.setVisible(true);
        tpBossTrainersSpinner.setEnabled(false);
        tpBossTrainersSpinner.setValue(1);
        tpImportantTrainersSpinner.setVisible(true);
        tpImportantTrainersSpinner.setEnabled(false);
        tpImportantTrainersSpinner.setValue(1);
        tpRegularTrainersSpinner.setVisible(true);
        tpRegularTrainersSpinner.setEnabled(false);
        tpRegularTrainersSpinner.setValue(1);
        tpAdditionalPokemonForLabel.setVisible(true);
        tpHeldItemsLabel.setVisible(true);
        tpBossTrainersItemsCheckBox.setVisible(true);
        tpBossTrainersItemsCheckBox.setEnabled(false);
        tpBossTrainersItemsCheckBox.setSelected(false);
        tpImportantTrainersItemsCheckBox.setVisible(true);
        tpImportantTrainersItemsCheckBox.setEnabled(false);
        tpImportantTrainersItemsCheckBox.setSelected(false);
        tpRegularTrainersItemsCheckBox.setVisible(true);
        tpRegularTrainersItemsCheckBox.setEnabled(false);
        tpRegularTrainersItemsCheckBox.setSelected(false);
        tpConsumableItemsOnlyCheckBox.setVisible(true);
        tpConsumableItemsOnlyCheckBox.setEnabled(false);
        tpConsumableItemsOnlyCheckBox.setSelected(false);
        tpScriptedHeldItemsCheckBox.setVisible(true);
        tpScriptedHeldItemsCheckBox.setEnabled(false);
        tpScriptedHeldItemsCheckBox.setSelected(false);
        tpSensibleItemsCheckBox.setVisible(true);
        tpSensibleItemsCheckBox.setEnabled(false);
        tpSensibleItemsCheckBox.setSelected(false);
        tpHighestLevelGetsItemCheckBox.setVisible(true);
        tpHighestLevelGetsItemCheckBox.setEnabled(false);
        tpHighestLevelGetsItemCheckBox.setSelected(false);
        tpRandomShinyTrainerPokemonCheckBox.setVisible(true);
        tpRandomShinyTrainerPokemonCheckBox.setEnabled(false);
        tpBetterMovesetsCheckBox.setVisible(true);
        tpBetterMovesetsCheckBox.setEnabled(false);
        tpBetterMovesetsCheckBox.setSelected(false);
        totpPanel.setVisible(true);
        totpAllyPanel.setVisible(true);
        totpAuraPanel.setVisible(true);
        totpUnchangedRadioButton.setVisible(true);
        totpUnchangedRadioButton.setEnabled(false);
        totpUnchangedRadioButton.setSelected(true);
        totpRandomRadioButton.setVisible(true);
        totpRandomRadioButton.setEnabled(false);
        totpRandomRadioButton.setSelected(false);
        totpRandomSimilarStrengthRadioButton.setVisible(true);
        totpRandomSimilarStrengthRadioButton.setEnabled(false);
        totpRandomSimilarStrengthRadioButton.setSelected(false);
        totpAllyUnchangedRadioButton.setVisible(true);
        totpAllyUnchangedRadioButton.setEnabled(false);
        totpAllyUnchangedRadioButton.setSelected(true);
        totpAllyRandomRadioButton.setVisible(true);
        totpAllyRandomRadioButton.setEnabled(false);
        totpAllyRandomRadioButton.setSelected(false);
        totpAllyRandomSimilarStrengthRadioButton.setVisible(true);
        totpAllyRandomSimilarStrengthRadioButton.setEnabled(false);
        totpAllyRandomSimilarStrengthRadioButton.setSelected(false);
        totpAuraUnchangedRadioButton.setVisible(true);
        totpAuraUnchangedRadioButton.setEnabled(false);
        totpAuraUnchangedRadioButton.setSelected(true);
        totpAuraRandomRadioButton.setVisible(true);
        totpAuraRandomRadioButton.setEnabled(false);
        totpAuraRandomRadioButton.setSelected(false);
        totpAuraRandomSameStrengthRadioButton.setVisible(true);
        totpAuraRandomSameStrengthRadioButton.setEnabled(false);
        totpAuraRandomSameStrengthRadioButton.setSelected(false);
        totpPercentageLevelModifierCheckBox.setVisible(true);
        totpPercentageLevelModifierCheckBox.setEnabled(false);
        totpPercentageLevelModifierCheckBox.setSelected(false);
        totpPercentageLevelModifierSlider.setVisible(true);
        totpPercentageLevelModifierSlider.setEnabled(false);
        totpPercentageLevelModifierSlider.setValue(0);
        totpRandomizeHeldItemsCheckBox.setVisible(true);
        totpRandomizeHeldItemsCheckBox.setEnabled(false);
        totpRandomizeHeldItemsCheckBox.setSelected(false);
        totpAllowAltFormesCheckBox.setVisible(true);
        totpAllowAltFormesCheckBox.setEnabled(false);
        totpAllowAltFormesCheckBox.setSelected(false);
        wpUnchangedRadioButton.setVisible(true);
        wpUnchangedRadioButton.setEnabled(false);
        wpUnchangedRadioButton.setSelected(false);
        wpRandomRadioButton.setVisible(true);
        wpRandomRadioButton.setEnabled(false);
        wpRandomRadioButton.setSelected(false);
        wpArea1To1RadioButton.setVisible(true);
        wpArea1To1RadioButton.setEnabled(false);
        wpArea1To1RadioButton.setSelected(false);
        wpGlobal1To1RadioButton.setVisible(true);
        wpGlobal1To1RadioButton.setEnabled(false);
        wpGlobal1To1RadioButton.setSelected(false);
        wpScriptedRadioButton.setVisible(true);
        wpScriptedRadioButton.setEnabled(false);
        wpScriptedRadioButton.setSelected(false);
        wpARNoneRadioButton.setVisible(true);
        wpARNoneRadioButton.setEnabled(false);
        wpARNoneRadioButton.setSelected(false);
        wpARSimilarStrengthRadioButton.setVisible(true);
        wpARSimilarStrengthRadioButton.setEnabled(false);
        wpARSimilarStrengthRadioButton.setSelected(false);
        wpARCatchEmAllModeRadioButton.setVisible(true);
        wpARCatchEmAllModeRadioButton.setEnabled(false);
        wpARCatchEmAllModeRadioButton.setSelected(false);
        wpARTypeThemeAreasRadioButton.setVisible(true);
        wpARTypeThemeAreasRadioButton.setEnabled(false);
        wpARTypeThemeAreasRadioButton.setSelected(false);
        wpUseTimeBasedEncountersCheckBox.setVisible(true);
        wpUseTimeBasedEncountersCheckBox.setEnabled(false);
        wpUseTimeBasedEncountersCheckBox.setSelected(false);
        wpDontUseLegendariesCheckBox.setVisible(true);
        wpDontUseLegendariesCheckBox.setEnabled(false);
        wpDontUseLegendariesCheckBox.setSelected(false);
        wpSetMinimumCatchRateCheckBox.setVisible(true);
        wpSetMinimumCatchRateCheckBox.setEnabled(false);
        wpSetMinimumCatchRateCheckBox.setSelected(false);
        wpRandomizeHeldItemsCheckBox.setVisible(true);
        wpRandomizeHeldItemsCheckBox.setEnabled(false);
        wpRandomizeHeldItemsCheckBox.setSelected(false);
        wpBanBadItemsCheckBox.setVisible(true);
        wpBanBadItemsCheckBox.setEnabled(false);
        wpBanBadItemsCheckBox.setSelected(false);
        wpScriptHeldItemsCheckBox.setVisible(true);
        wpScriptHeldItemsCheckBox.setEnabled(false);
        wpScriptHeldItemsCheckBox.setSelected(false);
        wpBalanceShakingGrassPokemonCheckBox.setVisible(true);
        wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        wpBalanceShakingGrassPokemonCheckBox.setSelected(false);
        wpPercentageLevelModifierCheckBox.setVisible(true);
        wpPercentageLevelModifierCheckBox.setEnabled(false);
        wpPercentageLevelModifierCheckBox.setSelected(false);
        wpPercentageLevelModifierSlider.setVisible(true);
        wpPercentageLevelModifierSlider.setEnabled(false);
        wpPercentageLevelModifierSlider.setValue(0);
        wpSetMinimumCatchRateSlider.setVisible(true);
        wpSetMinimumCatchRateSlider.setEnabled(false);
        wpSetMinimumCatchRateSlider.setValue(wpSetMinimumCatchRateSlider.getMinimum());
        wpAllowAltFormesCheckBox.setVisible(true);
        wpAllowAltFormesCheckBox.setEnabled(false);
        wpAllowAltFormesCheckBox.setSelected(false);
        tmUnchangedRadioButton.setVisible(true);
        tmUnchangedRadioButton.setEnabled(false);
        tmUnchangedRadioButton.setSelected(false);
        tmRandomRadioButton.setVisible(true);
        tmRandomRadioButton.setEnabled(false);
        tmRandomRadioButton.setSelected(false);
        tmScriptedRadioButton.setVisible(true);
        tmScriptedRadioButton.setEnabled(false);
        tmScriptedRadioButton.setSelected(false);
        tmNoGameBreakingMovesCheckBox.setVisible(true);
        tmNoGameBreakingMovesCheckBox.setEnabled(false);
        tmNoGameBreakingMovesCheckBox.setSelected(false);
        tmFullHMCompatibilityCheckBox.setVisible(true);
        tmFullHMCompatibilityCheckBox.setEnabled(false);
        tmFullHMCompatibilityCheckBox.setSelected(false);
        tmLevelupMoveSanityCheckBox.setVisible(true);
        tmLevelupMoveSanityCheckBox.setEnabled(false);
        tmLevelupMoveSanityCheckBox.setSelected(false);
        tmKeepFieldMoveTMsCheckBox.setVisible(true);
        tmKeepFieldMoveTMsCheckBox.setEnabled(false);
        tmKeepFieldMoveTMsCheckBox.setSelected(false);
        tmForceGoodDamagingCheckBox.setVisible(true);
        tmForceGoodDamagingCheckBox.setEnabled(false);
        tmForceGoodDamagingCheckBox.setSelected(false);
        tmForceGoodDamagingSlider.setVisible(true);
        tmForceGoodDamagingSlider.setEnabled(false);
        tmForceGoodDamagingSlider.setValue(tmForceGoodDamagingSlider.getMinimum());
        tmFollowEvolutionsCheckBox.setVisible(true);
        tmFollowEvolutionsCheckBox.setEnabled(false);
        tmFollowEvolutionsCheckBox.setSelected(false);
        thcUnchangedRadioButton.setVisible(true);
        thcUnchangedRadioButton.setEnabled(false);
        thcUnchangedRadioButton.setSelected(false);
        thcRandomPreferSameTypeRadioButton.setVisible(true);
        thcRandomPreferSameTypeRadioButton.setEnabled(false);
        thcRandomPreferSameTypeRadioButton.setSelected(false);
        thcRandomCompletelyRadioButton.setVisible(true);
        thcRandomCompletelyRadioButton.setEnabled(false);
        thcRandomCompletelyRadioButton.setSelected(false);
        thcFullCompatibilityRadioButton.setVisible(true);
        thcFullCompatibilityRadioButton.setEnabled(false);
        thcFullCompatibilityRadioButton.setSelected(false);
        thcScriptedRadioButton.setVisible(true);
        thcScriptedRadioButton.setEnabled(false);
        thcScriptedRadioButton.setSelected(false);
        mtUnchangedRadioButton.setVisible(true);
        mtUnchangedRadioButton.setEnabled(false);
        mtUnchangedRadioButton.setSelected(false);
        mtRandomRadioButton.setVisible(true);
        mtRandomRadioButton.setEnabled(false);
        mtRandomRadioButton.setSelected(false);
        mtScriptedRadioButton.setVisible(true);
        mtScriptedRadioButton.setEnabled(false);
        mtScriptedRadioButton.setSelected(false);
        mtNoGameBreakingMovesCheckBox.setVisible(true);
        mtNoGameBreakingMovesCheckBox.setEnabled(false);
        mtNoGameBreakingMovesCheckBox.setSelected(false);
        mtLevelupMoveSanityCheckBox.setVisible(true);
        mtLevelupMoveSanityCheckBox.setEnabled(false);
        mtLevelupMoveSanityCheckBox.setSelected(false);
        mtKeepFieldMoveTutorsCheckBox.setVisible(true);
        mtKeepFieldMoveTutorsCheckBox.setEnabled(false);
        mtKeepFieldMoveTutorsCheckBox.setSelected(false);
        mtForceGoodDamagingCheckBox.setVisible(true);
        mtForceGoodDamagingCheckBox.setEnabled(false);
        mtForceGoodDamagingCheckBox.setSelected(false);
        mtForceGoodDamagingSlider.setVisible(true);
        mtForceGoodDamagingSlider.setEnabled(false);
        mtForceGoodDamagingSlider.setValue(mtForceGoodDamagingSlider.getMinimum());
        mtFollowEvolutionsCheckBox.setVisible(true);
        mtFollowEvolutionsCheckBox.setEnabled(false);
        mtFollowEvolutionsCheckBox.setSelected(false);
        mtcUnchangedRadioButton.setVisible(true);
        mtcUnchangedRadioButton.setEnabled(false);
        mtcUnchangedRadioButton.setSelected(false);
        mtcRandomPreferSameTypeRadioButton.setVisible(true);
        mtcRandomPreferSameTypeRadioButton.setEnabled(false);
        mtcRandomPreferSameTypeRadioButton.setSelected(false);
        mtcRandomCompletelyRadioButton.setVisible(true);
        mtcRandomCompletelyRadioButton.setEnabled(false);
        mtcRandomCompletelyRadioButton.setSelected(false);
        mtcFullCompatibilityRadioButton.setVisible(true);
        mtcFullCompatibilityRadioButton.setEnabled(false);
        mtcFullCompatibilityRadioButton.setSelected(false);
        mtcScriptedRadioButton.setVisible(true);
        mtcScriptedRadioButton.setEnabled(false);
        mtcScriptedRadioButton.setSelected(false);
        fiUnchangedRadioButton.setVisible(true);
        fiUnchangedRadioButton.setEnabled(false);
        fiUnchangedRadioButton.setSelected(false);
        fiShuffleRadioButton.setVisible(true);
        fiShuffleRadioButton.setEnabled(false);
        fiShuffleRadioButton.setSelected(false);
        fiRandomRadioButton.setVisible(true);
        fiRandomRadioButton.setEnabled(false);
        fiRandomRadioButton.setSelected(false);
        fiRandomEvenDistributionRadioButton.setVisible(true);
        fiRandomEvenDistributionRadioButton.setEnabled(false);
        fiRandomEvenDistributionRadioButton.setSelected(false);
        fiScriptedRadioButton.setVisible(true);
        fiScriptedRadioButton.setEnabled(false);
        fiScriptedRadioButton.setSelected(false);
        fiBanBadItemsCheckBox.setVisible(true);
        fiBanBadItemsCheckBox.setEnabled(false);
        fiBanBadItemsCheckBox.setSelected(false);
        fiShuffleItemsCheckBox.setVisible(true);
        fiShuffleItemsCheckBox.setEnabled(false);
        fiShuffleItemsCheckBox.setSelected(false);
        shUnchangedRadioButton.setVisible(true);
        shUnchangedRadioButton.setEnabled(false);
        shUnchangedRadioButton.setSelected(false);
        shShuffleRadioButton.setVisible(true);
        shShuffleRadioButton.setEnabled(false);
        shShuffleRadioButton.setSelected(false);
        shRandomRadioButton.setVisible(true);
        shRandomRadioButton.setEnabled(false);
        shRandomRadioButton.setSelected(false);
        shScriptedRadioButton.setVisible(true);
        shScriptedRadioButton.setEnabled(false);
        shScriptedRadioButton.setSelected(false);
        shBanOverpoweredShopItemsCheckBox.setVisible(true);
        shBanOverpoweredShopItemsCheckBox.setEnabled(false);
        shBanOverpoweredShopItemsCheckBox.setSelected(false);
        shBanBadItemsCheckBox.setVisible(true);
        shBanBadItemsCheckBox.setEnabled(false);
        shBanBadItemsCheckBox.setSelected(false);
        shBanRegularShopItemsCheckBox.setVisible(true);
        shBanRegularShopItemsCheckBox.setEnabled(false);
        shBanRegularShopItemsCheckBox.setSelected(false);
        shBalanceShopItemPricesCheckBox.setVisible(true);
        shBalanceShopItemPricesCheckBox.setEnabled(false);
        shBalanceShopItemPricesCheckBox.setSelected(false);
        shGuaranteeEvolutionItemsCheckBox.setVisible(true);
        shGuaranteeEvolutionItemsCheckBox.setEnabled(false);
        shGuaranteeEvolutionItemsCheckBox.setSelected(false);
        shGuaranteeXItemsCheckBox.setVisible(true);
        shGuaranteeXItemsCheckBox.setEnabled(false);
        shGuaranteeXItemsCheckBox.setSelected(false);
        shScriptedPricesCheckbox.setVisible(true);
        shScriptedPricesCheckbox.setEnabled(false);
        shScriptedPricesCheckbox.setSelected(false);
        puUnchangedRadioButton.setVisible(true);
        puUnchangedRadioButton.setEnabled(false);
        puUnchangedRadioButton.setSelected(false);
        puRandomRadioButton.setVisible(true);
        puRandomRadioButton.setEnabled(false);
        puRandomRadioButton.setSelected(false);
        puScriptedRadioButton.setVisible(true);
        puScriptedRadioButton.setEnabled(false);
        puScriptedRadioButton.setSelected(false);
        puBanBadItemsCheckBox.setVisible(true);
        puBanBadItemsCheckBox.setEnabled(false);
        puBanBadItemsCheckBox.setSelected(false);
        miscBWExpPatchCheckBox.setVisible(true);
        miscBWExpPatchCheckBox.setEnabled(false);
        miscBWExpPatchCheckBox.setSelected(false);
        miscNerfXAccuracyCheckBox.setVisible(true);
        miscNerfXAccuracyCheckBox.setEnabled(false);
        miscNerfXAccuracyCheckBox.setSelected(false);
        miscFixCritRateCheckBox.setVisible(true);
        miscFixCritRateCheckBox.setEnabled(false);
        miscFixCritRateCheckBox.setSelected(false);
        miscFastestTextCheckBox.setVisible(true);
        miscFastestTextCheckBox.setEnabled(false);
        miscFastestTextCheckBox.setSelected(false);
        miscRunningShoesIndoorsCheckBox.setVisible(true);
        miscRunningShoesIndoorsCheckBox.setEnabled(false);
        miscRunningShoesIndoorsCheckBox.setSelected(false);
        miscRandomizePCPotionCheckBox.setVisible(true);
        miscRandomizePCPotionCheckBox.setEnabled(false);
        miscRandomizePCPotionCheckBox.setSelected(false);
        miscAllowPikachuEvolutionCheckBox.setVisible(true);
        miscAllowPikachuEvolutionCheckBox.setEnabled(false);
        miscAllowPikachuEvolutionCheckBox.setSelected(false);
        miscGiveNationalDexAtCheckBox.setVisible(true);
        miscGiveNationalDexAtCheckBox.setEnabled(false);
        miscGiveNationalDexAtCheckBox.setSelected(false);
        miscUpdateTypeEffectivenessCheckBox.setVisible(true);
        miscUpdateTypeEffectivenessCheckBox.setEnabled(false);
        miscUpdateTypeEffectivenessCheckBox.setSelected(false);
        miscLowerCasePokemonNamesCheckBox.setVisible(true);
        miscLowerCasePokemonNamesCheckBox.setEnabled(false);
        miscLowerCasePokemonNamesCheckBox.setSelected(false);
        miscRandomizeCatchingTutorialCheckBox.setVisible(true);
        miscRandomizeCatchingTutorialCheckBox.setEnabled(false);
        miscRandomizeCatchingTutorialCheckBox.setSelected(false);
        miscBanLuckyEggCheckBox.setVisible(true);
        miscBanLuckyEggCheckBox.setEnabled(false);
        miscBanLuckyEggCheckBox.setSelected(false);
        miscNoFreeLuckyEggCheckBox.setVisible(true);
        miscNoFreeLuckyEggCheckBox.setEnabled(false);
        miscNoFreeLuckyEggCheckBox.setSelected(false);
        miscBanBigMoneyManiacCheckBox.setVisible(true);
        miscBanBigMoneyManiacCheckBox.setEnabled(false);
        miscBanBigMoneyManiacCheckBox.setSelected(false);
        mtNoExistLabel.setVisible(false);
        mtNoneAvailableLabel.setVisible(false);

        liveTweaksPanel.setVisible(false);
        miscTweaksPanel.setVisible(true);

        sScriptInput.setVisible(true);
        sScriptInput.setEnabled(false);
    }

    private void romLoaded() {

        try {
            int pokemonGeneration = romHandler.generationOfPokemon();

            setRomNameLabel();
            romCodeLabel.setText(romHandler.getROMCode());
            romSupportLabel.setText(bundle.getString("GUI.romSupportPrefix") + " "
                    + this.romHandler.getSupportLevel());

            if (!romHandler.isRomValid()) {
                romNameLabel.setForeground(Color.RED);
                romCodeLabel.setForeground(Color.RED);
                romSupportLabel.setForeground(Color.RED);
                romSupportLabel.setText("<html>" + bundle.getString("GUI.romSupportPrefix") + " <b>Unofficial ROM</b>");
                showInvalidRomPopup();
            } else {
                romNameLabel.setForeground(Color.BLACK);
                romCodeLabel.setForeground(Color.BLACK);
                romSupportLabel.setForeground(Color.BLACK);
            }

            limitPokemonCheckBox.setVisible(true);
            limitPokemonCheckBox.setEnabled(true);
            limitPokemonButton.setVisible(true);
            limitPokemonScriptingCheckbox.setVisible(true);
            limitPokemonScriptingCheckbox.setEnabled(true);

            noIrregularAltFormesCheckBox.setVisible(pokemonGeneration >= 4);
            noIrregularAltFormesCheckBox.setEnabled(pokemonGeneration >= 4);

            raceModeCheckBox.setEnabled(true);

            loadSettingsButton.setEnabled(true);
            saveSettingsButton.setEnabled(true);

            // Pokemon Traits

            // Pokemon Base Statistics
            pbsUnchangedRadioButton.setEnabled(true);
            pbsUnchangedRadioButton.setSelected(true);
            pbsShuffleRadioButton.setEnabled(true);
            pbsRandomRadioButton.setEnabled(true);
            pbsScriptedRadioButton.setEnabled(true);

            pbsUnchangedEXPCurveRadioButton.setEnabled(true);
            pbsUnchangedEXPCurveRadioButton.setSelected(true);
            pbsScriptedEXPCurveRadioButton.setEnabled(true);
            pbsStandardizeEXPCurvesRadioButton.setEnabled(true);
            pbsLegendariesSlowRadioButton.setSelected(true);
            pbsUpdateBaseStatsCheckBox.setEnabled(pokemonGeneration < 8);
            pbsFollowMegaEvosCheckBox.setVisible(romHandler.hasMegaEvolutions());
            pbsUpdateComboBox.setVisible(pokemonGeneration < 8);
            ExpCurve[] expCurves = getEXPCurvesForGeneration(pokemonGeneration);
            String[] expCurveNames = new String[expCurves.length];
            for (int i = 0; i < expCurves.length; i++) {
                expCurveNames[i] = expCurves[i].toString();
            }
            pbsEXPCurveComboBox.setModel(new DefaultComboBoxModel<>(expCurveNames));
            pbsEXPCurveComboBox.setSelectedIndex(0);

            // Pokemon Types
            ptUnchangedRadioButton.setEnabled(true);
            ptUnchangedRadioButton.setSelected(true);
            ptRandomFollowEvolutionsRadioButton.setEnabled(true);
            ptRandomCompletelyRadioButton.setEnabled(true);
            ptScriptedRadioButton.setEnabled(true);
            ptFollowMegaEvosCheckBox.setVisible(romHandler.hasMegaEvolutions());
            ptIsDualTypeCheckBox.setEnabled(false);

            // Pokemon Abilities
            if (pokemonGeneration >= 3) {
                paUnchangedRadioButton.setEnabled(true);
                paUnchangedRadioButton.setSelected(true);
                paRandomRadioButton.setEnabled(true);
                paScriptedRadioButton.setEnabled(true);

                paAllowWonderGuardCheckBox.setEnabled(false);
                paFollowEvolutionsCheckBox.setEnabled(false);
                paTrappingAbilitiesCheckBox.setEnabled(false);
                paNegativeAbilitiesCheckBox.setEnabled(false);
                paBadAbilitiesCheckBox.setEnabled(false);
                paFollowMegaEvosCheckBox.setVisible(romHandler.hasMegaEvolutions());
                paWeighDuplicatesTogetherCheckBox.setEnabled(false);
                paEnsureTwoAbilitiesCheckbox.setEnabled(false);
            } else {
                pokemonAbilitiesPanel.setVisible(false);
            }

            // Pokemon Evolutions
            peUnchangedRadioButton.setEnabled(true);
            peUnchangedRadioButton.setSelected(true);
            peRandomRadioButton.setEnabled(true);
            peScriptedRadioButton.setEnabled(true);
            peRandomEveryLevelRadioButton.setVisible(pokemonGeneration >= 3);
            peRandomEveryLevelRadioButton.setEnabled(pokemonGeneration >= 3);
            peChangeImpossibleEvosCheckBox.setEnabled(true);
            peMakeEvolutionsEasierCheckBox.setEnabled(true);
            peRemoveTimeBasedEvolutionsCheckBox.setEnabled(true);
            peAllowAltFormesCheckBox.setVisible(pokemonGeneration >= 7);

            // Starters, Statics & Trades

            // Starter Pokemon
            spUnchangedRadioButton.setEnabled(true);
            spUnchangedRadioButton.setSelected(true);

            spCustomRadioButton.setEnabled(true);
            spRandomCompletelyRadioButton.setEnabled(true);
            spRandomTwoEvosRadioButton.setEnabled(true);
            spScriptedRadioButton.setEnabled(true);
            spAllowAltFormesCheckBox.setVisible(romHandler.hasStarterAltFormes());
            if (romHandler.isYellow()) {
                spComboBox3.setVisible(false);
            }
            populateDropdowns();

            boolean hasStarterHeldItems = (pokemonGeneration == 2 || pokemonGeneration == 3);
            spUnchangedStarterHeldItemsRadioButton.setEnabled(hasStarterHeldItems);
            spUnchangedStarterHeldItemsRadioButton.setVisible(hasStarterHeldItems);
            spRandomizeStarterHeldItemsRadioButton.setEnabled(hasStarterHeldItems);
            spRandomizeStarterHeldItemsRadioButton.setVisible(hasStarterHeldItems);
            spScriptedStarterHeldItemsRadioButton.setEnabled(hasStarterHeldItems);
            spScriptedStarterHeldItemsRadioButton.setVisible(hasStarterHeldItems);
            spBanBadItemsCheckBox.setEnabled(false);
            spBanBadItemsCheckBox.setVisible(hasStarterHeldItems);

            stpUnchangedRadioButton.setEnabled(true);
            stpUnchangedRadioButton.setSelected(true);
            if (romHandler.canChangeStaticPokemon()) {
                stpSwapLegendariesSwapStandardsRadioButton.setEnabled(true);
                stpRandomCompletelyRadioButton.setEnabled(true);
                stpRandomSimilarStrengthRadioButton.setEnabled(true);
                stpScriptedRadioButton.setEnabled(true);
                stpLimitMainGameLegendariesCheckBox.setVisible(romHandler.hasMainGameLegendaries());
                stpLimitMainGameLegendariesCheckBox.setEnabled(false);
                stpAllowAltFormesCheckBox.setVisible(romHandler.hasStaticAltFormes());
                stpSwapMegaEvosCheckBox.setVisible(pokemonGeneration == 6 && !romHandler.forceSwapStaticMegaEvos());
                stpPercentageLevelModifierCheckBox.setVisible(true);
                stpPercentageLevelModifierCheckBox.setEnabled(true);
                stpPercentageLevelModifierSlider.setVisible(true);
                stpPercentageLevelModifierSlider.setEnabled(false);
                stpFixMusicCheckBox.setVisible(romHandler.hasStaticMusicFix());
                stpFixMusicCheckBox.setEnabled(false);
            } else {
                stpSwapLegendariesSwapStandardsRadioButton.setVisible(false);
                stpRandomCompletelyRadioButton.setVisible(false);
                stpRandomSimilarStrengthRadioButton.setVisible(false);
                stpScriptedRadioButton.setVisible(false);
                stpRandomize600BSTCheckBox.setVisible(false);
                stpLimitMainGameLegendariesCheckBox.setVisible(false);
                stpPercentageLevelModifierCheckBox.setVisible(false);
                stpPercentageLevelModifierSlider.setVisible(false);
                stpFixMusicCheckBox.setVisible(false);
            }

            igtUnchangedRadioButton.setEnabled(true);
            igtUnchangedRadioButton.setSelected(true);
            igtRandomizeGivenPokemonOnlyRadioButton.setEnabled(true);
            igtRandomizeBothRequestedGivenRadioButton.setEnabled(true);
            igtScriptedRadioButton.setEnabled(true);

            igtRandomizeNicknamesCheckBox.setEnabled(false);
            igtRandomizeOTsCheckBox.setEnabled(false);
            igtRandomizeIVsCheckBox.setEnabled(false);
            igtRandomizeItemsCheckBox.setEnabled(false);

            if (pokemonGeneration == 1) {
                igtRandomizeOTsCheckBox.setVisible(false);
                igtRandomizeIVsCheckBox.setVisible(false);
                igtRandomizeItemsCheckBox.setVisible(false);
            }

            // Move Data
            mdRandomizeMovePowerCheckBox.setEnabled(true);
            mdRandomizeMoveAccuracyCheckBox.setEnabled(true);
            mdRandomizeMovePPCheckBox.setEnabled(true);
            mdRandomizeMoveTypesCheckBox.setEnabled(true);
            mdScriptedCheckBox.setEnabled(true);
            mdRandomizeMoveCategoryCheckBox.setEnabled(romHandler.hasPhysicalSpecialSplit());
            mdRandomizeMoveCategoryCheckBox.setVisible(romHandler.hasPhysicalSpecialSplit());
            mdUpdateMovesCheckBox.setEnabled(pokemonGeneration < 8);
            mdUpdateMovesCheckBox.setVisible(pokemonGeneration < 8);

            // Pokemon Movesets
            pmsUnchangedRadioButton.setEnabled(true);
            pmsUnchangedRadioButton.setSelected(true);
            pmsRandomPreferringSameTypeRadioButton.setEnabled(true);
            pmsRandomCompletelyRadioButton.setEnabled(true);
            pmsMetronomeOnlyModeRadioButton.setEnabled(true);

            pmsScriptLearntCheckBox.setEnabled(true);
            pmsScriptEggCheckBox.setEnabled(true);

            pmsGuaranteedLevel1MovesCheckBox.setVisible(romHandler.supportsFourStartingMoves());
            pmsGuaranteedLevel1MovesSlider.setVisible(romHandler.supportsFourStartingMoves());
            pmsEvolutionMovesCheckBox.setVisible(pokemonGeneration >= 7);

            tpComboBox.setEnabled(true);
            tpAllowAlternateFormesCheckBox.setVisible(romHandler.hasFunctionalFormes());
            tpForceFullyEvolvedAtCheckBox.setEnabled(true);
            tpPercentageLevelModifierCheckBox.setEnabled(true);
            tpSwapMegaEvosCheckBox.setVisible(romHandler.hasMegaEvolutions());
            tpDoubleBattleModeCheckBox.setVisible(pokemonGeneration >= 3);

            boolean additionalPokemonAvailable = pokemonGeneration >= 3;

            tpAdditionalPokemonForLabel.setVisible(additionalPokemonAvailable);
            tpBossTrainersCheckBox.setVisible(additionalPokemonAvailable);
            tpBossTrainersCheckBox.setEnabled(false);
            tpBossTrainersSpinner.setVisible(additionalPokemonAvailable);
            tpImportantTrainersCheckBox.setVisible(additionalPokemonAvailable);
            tpImportantTrainersCheckBox.setEnabled(false);
            tpImportantTrainersSpinner.setVisible(additionalPokemonAvailable);
            tpRegularTrainersCheckBox.setVisible(additionalPokemonAvailable);
            tpRegularTrainersCheckBox.setEnabled(false);
            tpRegularTrainersSpinner.setVisible(additionalPokemonAvailable);

            boolean trainersHeldItemSupport = pokemonGeneration >= 3;
            tpHeldItemsLabel.setVisible(trainersHeldItemSupport);
            tpBossTrainersItemsCheckBox.setVisible(trainersHeldItemSupport);
            tpBossTrainersItemsCheckBox.setEnabled(false);
            tpImportantTrainersItemsCheckBox.setVisible(trainersHeldItemSupport);
            tpImportantTrainersItemsCheckBox.setEnabled(false);
            tpRegularTrainersItemsCheckBox.setVisible(trainersHeldItemSupport);
            tpRegularTrainersItemsCheckBox.setEnabled(false);
            tpConsumableItemsOnlyCheckBox.setVisible(trainersHeldItemSupport);
            tpConsumableItemsOnlyCheckBox.setEnabled(false);
            tpScriptedHeldItemsCheckBox.setVisible(trainersHeldItemSupport);
            tpScriptedHeldItemsCheckBox.setEnabled(false);
            tpSensibleItemsCheckBox.setVisible(trainersHeldItemSupport);
            tpSensibleItemsCheckBox.setEnabled(false);
            tpHighestLevelGetsItemCheckBox.setVisible(trainersHeldItemSupport);
            tpHighestLevelGetsItemCheckBox.setEnabled(false);

            tpEliteFourUniquePokemonCheckBox.setVisible(pokemonGeneration >= 3);
            tpEliteFourUniquePokemonSpinner.setVisible(pokemonGeneration >= 3);

            tpRandomizeTrainerNamesCheckBox.setEnabled(true);
            tpRandomizeTrainerClassNamesCheckBox.setEnabled(true);
            tpNoEarlyWonderGuardCheckBox.setVisible(pokemonGeneration >= 3);
            tpRandomShinyTrainerPokemonCheckBox.setVisible(pokemonGeneration >= 7);
            tpBetterMovesetsCheckBox.setVisible(pokemonGeneration >= 3);

            totpPanel.setVisible(pokemonGeneration == 7);
            if (totpPanel.isVisible()) {
                totpUnchangedRadioButton.setEnabled(true);
                totpRandomRadioButton.setEnabled(true);
                totpRandomSimilarStrengthRadioButton.setEnabled(true);

                totpAllyPanel.setVisible(pokemonGeneration == 7);
                totpAllyUnchangedRadioButton.setEnabled(true);
                totpAllyRandomRadioButton.setEnabled(true);
                totpAllyRandomSimilarStrengthRadioButton.setEnabled(true);

                totpAuraPanel.setVisible(pokemonGeneration == 7);
                totpAuraUnchangedRadioButton.setEnabled(true);
                totpAuraRandomRadioButton.setEnabled(true);
                totpAuraRandomSameStrengthRadioButton.setEnabled(true);

                totpRandomizeHeldItemsCheckBox.setEnabled(true);
                totpAllowAltFormesCheckBox.setEnabled(false);
                totpPercentageLevelModifierCheckBox.setEnabled(true);
                totpPercentageLevelModifierSlider.setEnabled(false);
            }

            // Wild Pokemon
            wpUnchangedRadioButton.setEnabled(true);
            wpUnchangedRadioButton.setSelected(true);
            wpRandomRadioButton.setEnabled(true);
            wpArea1To1RadioButton.setEnabled(true);
            wpGlobal1To1RadioButton.setEnabled(true);
            wpScriptedRadioButton.setEnabled(true);

            wpARNoneRadioButton.setSelected(true);

            wpUseTimeBasedEncountersCheckBox.setVisible(romHandler.hasTimeBasedEncounters());
            wpSetMinimumCatchRateCheckBox.setEnabled(true);
            wpRandomizeHeldItemsCheckBox.setEnabled(true);
            wpRandomizeHeldItemsCheckBox.setVisible(pokemonGeneration != 1);
            wpScriptHeldItemsCheckBox.setVisible(wpRandomizeHeldItemsCheckBox.isVisible());
            wpBanBadItemsCheckBox.setVisible(pokemonGeneration != 1);
            wpBalanceShakingGrassPokemonCheckBox.setVisible(pokemonGeneration == 5);
            wpPercentageLevelModifierCheckBox.setEnabled(true);
            wpAllowAltFormesCheckBox.setVisible(romHandler.hasWildAltFormes());

            tmUnchangedRadioButton.setEnabled(true);
            tmUnchangedRadioButton.setSelected(true);
            tmRandomRadioButton.setEnabled(true);
            tmScriptedRadioButton.setEnabled(true);
            tmFullHMCompatibilityCheckBox.setVisible(pokemonGeneration < 7);
            if (tmFullHMCompatibilityCheckBox.isVisible()) {
                tmFullHMCompatibilityCheckBox.setEnabled(true);
            }

            thcUnchangedRadioButton.setEnabled(true);
            thcUnchangedRadioButton.setSelected(true);
            thcRandomPreferSameTypeRadioButton.setEnabled(true);
            thcRandomCompletelyRadioButton.setEnabled(true);
            thcFullCompatibilityRadioButton.setEnabled(true);
            thcScriptedRadioButton.setEnabled(true);

            if (romHandler.hasMoveTutors()) {
                mtMovesPanel.setVisible(true);
                mtCompatPanel.setVisible(true);
                mtNoExistLabel.setVisible(false);

                mtUnchangedRadioButton.setEnabled(true);
                mtUnchangedRadioButton.setSelected(true);
                mtRandomRadioButton.setEnabled(true);
                mtScriptedRadioButton.setEnabled(true);

                mtcUnchangedRadioButton.setEnabled(true);
                mtcUnchangedRadioButton.setSelected(true);
                mtcRandomPreferSameTypeRadioButton.setEnabled(true);
                mtcRandomCompletelyRadioButton.setEnabled(true);
                mtcFullCompatibilityRadioButton.setEnabled(true);
                mtcScriptedRadioButton.setEnabled(true);
            } else {
                mtMovesPanel.setVisible(false);
                mtCompatPanel.setVisible(false);
                mtNoExistLabel.setVisible(true);
            }

            fiUnchangedRadioButton.setEnabled(true);
            fiUnchangedRadioButton.setSelected(true);
            fiShuffleRadioButton.setEnabled(true);
            fiRandomRadioButton.setEnabled(true);
            fiRandomEvenDistributionRadioButton.setEnabled(true);
            fiScriptedRadioButton.setEnabled(true);

            shopItemsPanel.setVisible(romHandler.hasShopRandomization());
            shUnchangedRadioButton.setEnabled(true);
            shUnchangedRadioButton.setSelected(true);
            shShuffleRadioButton.setEnabled(true);
            shScriptedRadioButton.setEnabled(true);
            shRandomRadioButton.setEnabled(true);
            shScriptedPricesCheckbox.setEnabled(true);

            pickupItemsPanel.setVisible(romHandler.abilitiesPerPokemon() > 0);
            puUnchangedRadioButton.setEnabled(true);
            puUnchangedRadioButton.setSelected(true);
            puRandomRadioButton.setEnabled(true);
            puScriptedRadioButton.setEnabled(true);

            sScriptInput.setEnabled(true);

            int mtsAvailable = romHandler.miscTweaksAvailable();
            int mtCount = MiscTweak.allTweaks.size();
            List<JCheckBox> usableCheckBoxes = new ArrayList<>();

            for (int mti = 0; mti < mtCount; mti++) {
                MiscTweak mt = MiscTweak.allTweaks.get(mti);
                JCheckBox mtCB = tweakCheckBoxes.get(mti);
                mtCB.setSelected(false);
                if ((mtsAvailable & mt.getValue()) != 0) {
                    mtCB.setVisible(true);
                    mtCB.setEnabled(true);
                    usableCheckBoxes.add(mtCB);
                } else {
                    mtCB.setVisible(false);
                    mtCB.setEnabled(false);
                }
            }

            if (usableCheckBoxes.size() > 0) {
                setTweaksPanel(usableCheckBoxes);
                //tabbedPane1.setComponentAt(7,makeTweaksLayout(usableCheckBoxes));
                //miscTweaksPanel.setLayout(makeTweaksLayout(usableCheckBoxes));
            } else {
                mtNoneAvailableLabel.setVisible(true);
                liveTweaksPanel.setVisible(false);
                miscTweaksPanel.setVisible(true);
                //miscTweaksPanel.setLayout(noTweaksLayout);
            }

            if (romHandler.generationOfPokemon() < 6) {
                applyGameUpdateMenuItem.setVisible(false);
            } else {
                applyGameUpdateMenuItem.setVisible(true);
            }

            if (romHandler.hasGameUpdateLoaded()) {
                removeGameUpdateMenuItem.setVisible(true);
            } else {
                removeGameUpdateMenuItem.setVisible(false);
            }

            gameMascotLabel.setIcon(makeMascotIcon());

            if (romHandler instanceof AbstractDSRomHandler) {
                ((AbstractDSRomHandler) romHandler).closeInnerRom();
            } else if (romHandler instanceof Abstract3DSRomHandler) {
                ((Abstract3DSRomHandler) romHandler).closeInnerRom();
            }
        } catch (Exception e) {
            attemptToLogException(e, "GUI.processFailed","GUI.processFailedNoLog", null, null);
            romHandler = null;
            initialState();
        }
    }

    private void setRomNameLabel() {
        if (romHandler.hasGameUpdateLoaded()) {
            romNameLabel.setText(romHandler.getROMName() + " (" + romHandler.getGameUpdateVersion() + ")");
        } else {
            romNameLabel.setText(romHandler.getROMName());
        }
    }

    private void setTweaksPanel(List<JCheckBox> usableCheckBoxes) {
        mtNoneAvailableLabel.setVisible(false);
        miscTweaksPanel.setVisible(false);
        baseTweaksPanel.remove(liveTweaksPanel);
        makeTweaksLayout(usableCheckBoxes);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;
        c.weighty = 0.1;
        c.gridx = 1;
        c.gridy = 1;
        baseTweaksPanel.add(liveTweaksPanel,c);
        liveTweaksPanel.setVisible(true);
    }

    private void enableOrDisableSubControls() {

        if (limitPokemonCheckBox.isSelected()) {
            limitPokemonButton.setEnabled(true);
        } else {
            limitPokemonButton.setEnabled(false);
        }

        boolean followEvolutionControlsEnabled = !peRandomEveryLevelRadioButton.isSelected();
        boolean followMegaEvolutionControlsEnabled = !(peRandomEveryLevelRadioButton.isSelected() && !noIrregularAltFormesCheckBox.isSelected() && peAllowAltFormesCheckBox.isSelected());

        if (peRandomEveryLevelRadioButton.isSelected()) {
            // If Evolve Every Level is enabled, unselect all "Follow Evolutions" controls
            pbsFollowEvolutionsCheckBox.setSelected(false);
            ptRandomFollowEvolutionsRadioButton.setEnabled(false);
            if (ptRandomFollowEvolutionsRadioButton.isSelected()) {
                ptRandomFollowEvolutionsRadioButton.setSelected(false);
                ptRandomCompletelyRadioButton.setSelected(true);
            }
            spRandomTwoEvosRadioButton.setEnabled(false);
            if (spRandomTwoEvosRadioButton.isSelected()) {
                spRandomTwoEvosRadioButton.setSelected(false);
                spRandomCompletelyRadioButton.setSelected(true);
            }
            paFollowEvolutionsCheckBox.setSelected(false);
            tmFollowEvolutionsCheckBox.setSelected(false);
            mtFollowEvolutionsCheckBox.setSelected(false);

            // If the Follow Mega Evolution controls should be disabled, deselect them here too
            if (!followMegaEvolutionControlsEnabled) {
                pbsFollowMegaEvosCheckBox.setSelected(false);
                ptFollowMegaEvosCheckBox.setSelected(false);
                paFollowMegaEvosCheckBox.setSelected(false);
            }

            // Also disable/unselect all the settings that make evolutions easier/possible,
            // since they aren't relevant in this scenario at all.
            peChangeImpossibleEvosCheckBox.setEnabled(false);
            peChangeImpossibleEvosCheckBox.setSelected(false);
            peMakeEvolutionsEasierCheckBox.setEnabled(false);
            peMakeEvolutionsEasierCheckBox.setSelected(false);
            peRemoveTimeBasedEvolutionsCheckBox.setEnabled(false);
            peRemoveTimeBasedEvolutionsCheckBox.setSelected(false);

            // Disable "Force Fully Evolved" Trainer Pokemon
            tpForceFullyEvolvedAtCheckBox.setSelected(false);
            tpForceFullyEvolvedAtCheckBox.setEnabled(false);
            tpForceFullyEvolvedAtSlider.setEnabled(false);
            tpForceFullyEvolvedAtSlider.setValue(tpForceFullyEvolvedAtSlider.getMinimum());
        } else {
            // All other "Follow Evolutions" controls get properly set/unset below
            // except this one, so manually enable it again.
            ptRandomFollowEvolutionsRadioButton.setEnabled(true);
            spRandomTwoEvosRadioButton.setEnabled(true);

            // The controls that make evolutions easier/possible, however,
            // need to all be manually re-enabled.
            peChangeImpossibleEvosCheckBox.setEnabled(true);
            peMakeEvolutionsEasierCheckBox.setEnabled(true);
            peRemoveTimeBasedEvolutionsCheckBox.setEnabled(true);

            // Re-enable "Force Fully Evolved" Trainer Pokemon
            tpForceFullyEvolvedAtCheckBox.setEnabled(true);
        }

        if (pbsUnchangedRadioButton.isSelected()) {
            pbsFollowEvolutionsCheckBox.setEnabled(false);
            pbsFollowEvolutionsCheckBox.setSelected(false);
            pbsFollowMegaEvosCheckBox.setEnabled(false);
            pbsFollowMegaEvosCheckBox.setSelected(false);
        } else {
            pbsFollowEvolutionsCheckBox.setEnabled(followEvolutionControlsEnabled);
            pbsFollowMegaEvosCheckBox.setEnabled(followMegaEvolutionControlsEnabled);
        }

        if (pbsRandomRadioButton.isSelected() || pbsScriptedRadioButton.isSelected()) {
            if (pbsFollowEvolutionsCheckBox.isSelected() || pbsFollowMegaEvosCheckBox.isSelected()) {
                pbsAssignEvoStatsRandomlyCheckBox.setEnabled(true);
            } else {
                pbsAssignEvoStatsRandomlyCheckBox.setEnabled(false);
                pbsAssignEvoStatsRandomlyCheckBox.setSelected(false);
            }
        } else {
            pbsAssignEvoStatsRandomlyCheckBox.setEnabled(false);
            pbsAssignEvoStatsRandomlyCheckBox.setSelected(false);
        }

        if (pbsStandardizeEXPCurvesRadioButton.isSelected()) {
            pbsLegendariesSlowRadioButton.setEnabled(true);
            pbsStrongLegendariesSlowRadioButton.setEnabled(true);
            pbsAllMediumFastRadioButton.setEnabled(true);
            pbsEXPCurveComboBox.setEnabled(true);
        } else {
            pbsLegendariesSlowRadioButton.setEnabled(false);
            pbsLegendariesSlowRadioButton.setSelected(true);
            pbsStrongLegendariesSlowRadioButton.setEnabled(false);
            pbsAllMediumFastRadioButton.setEnabled(false);
            pbsEXPCurveComboBox.setEnabled(false);
        }

        if (pbsUpdateBaseStatsCheckBox.isSelected()) {
            pbsUpdateComboBox.setEnabled(true);
        } else {
            pbsUpdateComboBox.setEnabled(false);
        }

        if (ptUnchangedRadioButton.isSelected()) {
            ptFollowMegaEvosCheckBox.setEnabled(false);
            ptFollowMegaEvosCheckBox.setSelected(false);
            ptIsDualTypeCheckBox.setEnabled(false);
            ptIsDualTypeCheckBox.setSelected(false);
        } else {
            ptFollowMegaEvosCheckBox.setEnabled(followMegaEvolutionControlsEnabled);
            ptIsDualTypeCheckBox.setEnabled(true);
        }

        //scripted pokemon types isn't compatible with "force dual types"
        //because it's too simple to implement in a script to warrant a complex way to force it when selected
        if(ptScriptedRadioButton.isSelected())
        {
            ptIsDualTypeCheckBox.setEnabled(false);
            ptIsDualTypeCheckBox.setSelected(false);
        }
        else{
            ptIsDualTypeCheckBox.setEnabled(!ptUnchangedRadioButton.isSelected());
        }

        if (paRandomRadioButton.isSelected() || paScriptedRadioButton.isSelected()) {
            paAllowWonderGuardCheckBox.setEnabled(true);
            paFollowEvolutionsCheckBox.setEnabled(followEvolutionControlsEnabled);
            paFollowMegaEvosCheckBox.setEnabled(followMegaEvolutionControlsEnabled);
            paTrappingAbilitiesCheckBox.setEnabled(true);
            paNegativeAbilitiesCheckBox.setEnabled(true);
            paBadAbilitiesCheckBox.setEnabled(true);
            paWeighDuplicatesTogetherCheckBox.setEnabled(true);
            if(paScriptedRadioButton.isSelected())
            {
                paEnsureTwoAbilitiesCheckbox.setEnabled(false);
                paEnsureTwoAbilitiesCheckbox.setSelected(false);
            }
            else{
                paEnsureTwoAbilitiesCheckbox.setEnabled(true);
            }
        } else {
            paAllowWonderGuardCheckBox.setEnabled(false);
            paAllowWonderGuardCheckBox.setSelected(false);
            paFollowEvolutionsCheckBox.setEnabled(false);
            paFollowEvolutionsCheckBox.setSelected(false);
            paTrappingAbilitiesCheckBox.setEnabled(false);
            paTrappingAbilitiesCheckBox.setSelected(false);
            paNegativeAbilitiesCheckBox.setEnabled(false);
            paNegativeAbilitiesCheckBox.setSelected(false);
            paBadAbilitiesCheckBox.setEnabled(false);
            paBadAbilitiesCheckBox.setSelected(false);
            paFollowMegaEvosCheckBox.setEnabled(false);
            paFollowMegaEvosCheckBox.setSelected(false);
            paWeighDuplicatesTogetherCheckBox.setEnabled(false);
            paWeighDuplicatesTogetherCheckBox.setSelected(false);
            paEnsureTwoAbilitiesCheckbox.setEnabled(false);
            paEnsureTwoAbilitiesCheckbox.setSelected(false);
        }

        if (peRandomRadioButton.isSelected()) {
            peSimilarStrengthCheckBox.setEnabled(true);
            peSameTypingCheckBox.setEnabled(true);
            peLimitEvolutionsToThreeCheckBox.setEnabled(true);
            peForceChangeCheckBox.setEnabled(true);
            peAllowAltFormesCheckBox.setEnabled(true);
        } else if (peRandomEveryLevelRadioButton.isSelected()) {
            peSimilarStrengthCheckBox.setEnabled(false);
            peSimilarStrengthCheckBox.setSelected(false);
            peSameTypingCheckBox.setEnabled(true);
            peLimitEvolutionsToThreeCheckBox.setEnabled(false);
            peLimitEvolutionsToThreeCheckBox.setSelected(false);
            peForceChangeCheckBox.setEnabled(true);
            peAllowAltFormesCheckBox.setEnabled(true);
        } else if(peScriptedRadioButton.isSelected()){
            peSimilarStrengthCheckBox.setEnabled(false);
            peSimilarStrengthCheckBox.setSelected(false);
            peSameTypingCheckBox.setEnabled(true);
            peLimitEvolutionsToThreeCheckBox.setEnabled(false);
            peLimitEvolutionsToThreeCheckBox.setSelected(false);
            peForceChangeCheckBox.setEnabled(true);
            peAllowAltFormesCheckBox.setEnabled(true);
        }else {
            peSimilarStrengthCheckBox.setEnabled(false);
            peSimilarStrengthCheckBox.setSelected(false);
            peSameTypingCheckBox.setEnabled(false);
            peSameTypingCheckBox.setSelected(false);
            peLimitEvolutionsToThreeCheckBox.setEnabled(false);
            peLimitEvolutionsToThreeCheckBox.setSelected(false);
            peForceChangeCheckBox.setEnabled(false);
            peForceChangeCheckBox.setSelected(false);
            peAllowAltFormesCheckBox.setEnabled(false);
            peAllowAltFormesCheckBox.setSelected(false);
        }

        boolean spCustomStatus = spCustomRadioButton.isSelected();
        spComboBox1.setEnabled(spCustomStatus);
        spComboBox2.setEnabled(spCustomStatus);
        spComboBox3.setEnabled(spCustomStatus);

        if (spUnchangedRadioButton.isSelected()) {
            spAllowAltFormesCheckBox.setEnabled(false);
            spAllowAltFormesCheckBox.setSelected(false);
        } else {
            spAllowAltFormesCheckBox.setEnabled(true);
        }

        if (spRandomizeStarterHeldItemsRadioButton.isSelected() || spScriptedStarterHeldItemsRadioButton.isSelected()) {
            spBanBadItemsCheckBox.setEnabled(true);
        } else {
            spBanBadItemsCheckBox.setEnabled(false);
            spBanBadItemsCheckBox.setSelected(false);
        }

        if (stpUnchangedRadioButton.isSelected()) {
            stpRandomize600BSTCheckBox.setEnabled(false);
            stpRandomize600BSTCheckBox.setSelected(false);
            stpAllowAltFormesCheckBox.setEnabled(false);
            stpAllowAltFormesCheckBox.setSelected(false);
            stpSwapMegaEvosCheckBox.setEnabled(false);
            stpSwapMegaEvosCheckBox.setSelected(false);
            stpFixMusicCheckBox.setEnabled(false);
            stpFixMusicCheckBox.setSelected(false);
        } else {
            stpRandomize600BSTCheckBox.setEnabled(true);
            stpAllowAltFormesCheckBox.setEnabled(true);
            stpSwapMegaEvosCheckBox.setEnabled(true);
            stpFixMusicCheckBox.setEnabled(true);
        }

        if (stpRandomSimilarStrengthRadioButton.isSelected()) {
            stpLimitMainGameLegendariesCheckBox.setEnabled(stpLimitMainGameLegendariesCheckBox.isVisible());
        } else {
            stpLimitMainGameLegendariesCheckBox.setEnabled(false);
            stpLimitMainGameLegendariesCheckBox.setSelected(false);
        }

        if(stpScriptedRadioButton.isSelected())
        {
            stpRandomize600BSTCheckBox.setEnabled(false);
            stpRandomize600BSTCheckBox.setSelected(false);
        }

        if (stpPercentageLevelModifierCheckBox.isSelected()) {
            stpPercentageLevelModifierSlider.setEnabled(true);
        } else {
            stpPercentageLevelModifierSlider.setEnabled(false);
            stpPercentageLevelModifierSlider.setValue(0);
        }

        if (igtUnchangedRadioButton.isSelected()) {
            igtRandomizeItemsCheckBox.setEnabled(false);
            igtRandomizeItemsCheckBox.setSelected(false);
            igtRandomizeIVsCheckBox.setEnabled(false);
            igtRandomizeIVsCheckBox.setSelected(false);
            igtRandomizeNicknamesCheckBox.setEnabled(false);
            igtRandomizeNicknamesCheckBox.setSelected(false);
            igtRandomizeOTsCheckBox.setEnabled(false);
            igtRandomizeOTsCheckBox.setSelected(false);
        } else {
            igtRandomizeItemsCheckBox.setEnabled(true);
            igtRandomizeIVsCheckBox.setEnabled(true);
            igtRandomizeNicknamesCheckBox.setEnabled(true);
            igtRandomizeOTsCheckBox.setEnabled(true);
        }

        if (mdUpdateMovesCheckBox.isSelected()) {
            mdUpdateComboBox.setEnabled(true);
        } else {
            mdUpdateComboBox.setEnabled(false);
        }

        if (pmsMetronomeOnlyModeRadioButton.isSelected() || pmsUnchangedRadioButton.isSelected()) {
            pmsGuaranteedLevel1MovesCheckBox.setEnabled(false);
            pmsGuaranteedLevel1MovesCheckBox.setSelected(false);
            pmsForceGoodDamagingCheckBox.setEnabled(false);
            pmsForceGoodDamagingCheckBox.setSelected(false);
            pmsReorderDamagingMovesCheckBox.setEnabled(false);
            pmsReorderDamagingMovesCheckBox.setSelected(false);
            pmsNoGameBreakingMovesCheckBox.setEnabled(false);
            pmsNoGameBreakingMovesCheckBox.setSelected(false);
            pmsEvolutionMovesCheckBox.setEnabled(false);
            pmsEvolutionMovesCheckBox.setSelected(false);
            pmsScriptLearnAfterCheckBox.setEnabled(false);
            pmsScriptLearnAfterCheckBox.setSelected(false);
        } else {
            pmsGuaranteedLevel1MovesCheckBox.setEnabled(true);
            pmsForceGoodDamagingCheckBox.setEnabled(true);
            pmsReorderDamagingMovesCheckBox.setEnabled(true);
            pmsNoGameBreakingMovesCheckBox.setEnabled(true);
            pmsEvolutionMovesCheckBox.setEnabled(true);
            pmsScriptLearnAfterCheckBox.setEnabled(true);
        }

        //scriping movesets shouldn't be available in metronome-only mode
        if(pmsMetronomeOnlyModeRadioButton.isSelected())
        {
            pmsScriptLearntCheckBox.setEnabled(false);
            pmsScriptLearntCheckBox.setSelected(false);
            pmsScriptEggCheckBox.setEnabled(false);
            pmsScriptEggCheckBox.setSelected(false);
            pmsScriptLearnAfterCheckBox.setEnabled(false);
            pmsScriptLearnAfterCheckBox.setSelected(false);
        }
        else
        {
            pmsScriptLearntCheckBox.setEnabled(true);
            pmsScriptEggCheckBox.setEnabled(true);
        }

        if (pmsGuaranteedLevel1MovesCheckBox.isSelected()) {
            pmsGuaranteedLevel1MovesSlider.setEnabled(true);
        } else {
            pmsGuaranteedLevel1MovesSlider.setEnabled(false);
            pmsGuaranteedLevel1MovesSlider.setValue(pmsGuaranteedLevel1MovesSlider.getMinimum());
        }

        if (pmsForceGoodDamagingCheckBox.isSelected()) {
            pmsForceGoodDamagingSlider.setEnabled(true);
        } else {
            pmsForceGoodDamagingSlider.setEnabled(false);
            pmsForceGoodDamagingSlider.setValue(pmsForceGoodDamagingSlider.getMinimum());
        }

        if (isTrainerSetting(TRAINER_UNCHANGED)) {
            tpSimilarStrengthCheckBox.setEnabled(false);
            tpSimilarStrengthCheckBox.setSelected(false);
            tpDontUseLegendariesCheckBox.setEnabled(false);
            tpDontUseLegendariesCheckBox.setSelected(false);
            tpNoEarlyWonderGuardCheckBox.setEnabled(false);
            tpNoEarlyWonderGuardCheckBox.setSelected(false);
            tpAllowAlternateFormesCheckBox.setEnabled(false);
            tpAllowAlternateFormesCheckBox.setSelected(false);
            tpSwapMegaEvosCheckBox.setEnabled(false);
            tpSwapMegaEvosCheckBox.setSelected(false);
            tpRandomShinyTrainerPokemonCheckBox.setEnabled(false);
            tpRandomShinyTrainerPokemonCheckBox.setSelected(false);
            tpBetterMovesetsCheckBox.setEnabled(false);
            tpBetterMovesetsCheckBox.setSelected(false);
            tpDoubleBattleModeCheckBox.setEnabled(false);
            tpDoubleBattleModeCheckBox.setSelected(false);
            tpBossTrainersCheckBox.setEnabled(false);
            tpBossTrainersCheckBox.setSelected(false);
            tpImportantTrainersCheckBox.setEnabled(false);
            tpImportantTrainersCheckBox.setSelected(false);
            tpRegularTrainersCheckBox.setEnabled(false);
            tpRegularTrainersCheckBox.setSelected(false);
            tpBossTrainersItemsCheckBox.setEnabled(false);
            tpBossTrainersItemsCheckBox.setSelected(false);
            tpImportantTrainersItemsCheckBox.setEnabled(false);
            tpImportantTrainersItemsCheckBox.setSelected(false);
            tpRegularTrainersItemsCheckBox.setEnabled(false);
            tpRegularTrainersItemsCheckBox.setSelected(false);
            tpConsumableItemsOnlyCheckBox.setEnabled(false);
            tpConsumableItemsOnlyCheckBox.setSelected(false);
            tpScriptedHeldItemsCheckBox.setEnabled(false);
            tpScriptedHeldItemsCheckBox.setSelected(false);
            tpSensibleItemsCheckBox.setEnabled(false);
            tpSensibleItemsCheckBox.setSelected(false);
            tpHighestLevelGetsItemCheckBox.setEnabled(false);
            tpHighestLevelGetsItemCheckBox.setSelected(false);
            tpEliteFourUniquePokemonCheckBox.setEnabled(false);
            tpEliteFourUniquePokemonCheckBox.setSelected(false);
        } else {
            tpSimilarStrengthCheckBox.setEnabled(true);
            tpDontUseLegendariesCheckBox.setEnabled(true);
            tpNoEarlyWonderGuardCheckBox.setEnabled(true);
            tpAllowAlternateFormesCheckBox.setEnabled(true);
            if (currentRestrictions == null || currentRestrictions.allowTrainerSwapMegaEvolvables(
                    romHandler.forceSwapStaticMegaEvos(), isTrainerSetting(TRAINER_TYPE_THEMED))) {
                tpSwapMegaEvosCheckBox.setEnabled(true);
            } else {
                tpSwapMegaEvosCheckBox.setEnabled(false);
                tpSwapMegaEvosCheckBox.setSelected(false);
            }
            tpRandomShinyTrainerPokemonCheckBox.setEnabled(true);
            tpBetterMovesetsCheckBox.setEnabled(true);
            tpDoubleBattleModeCheckBox.setEnabled(tpDoubleBattleModeCheckBox.isVisible());
            tpBossTrainersCheckBox.setEnabled(tpBossTrainersCheckBox.isVisible());
            tpImportantTrainersCheckBox.setEnabled(tpImportantTrainersCheckBox.isVisible());
            tpRegularTrainersCheckBox.setEnabled(tpRegularTrainersCheckBox.isVisible());
            tpBossTrainersItemsCheckBox.setEnabled(tpBossTrainersItemsCheckBox.isVisible());
            tpImportantTrainersItemsCheckBox.setEnabled(tpImportantTrainersItemsCheckBox.isVisible());
            tpRegularTrainersItemsCheckBox.setEnabled(tpRegularTrainersItemsCheckBox.isVisible());
            tpEliteFourUniquePokemonCheckBox.setEnabled(tpEliteFourUniquePokemonCheckBox.isVisible());
        }

        if (tpForceFullyEvolvedAtCheckBox.isSelected()) {
            tpForceFullyEvolvedAtSlider.setEnabled(true);
        } else {
            tpForceFullyEvolvedAtSlider.setEnabled(false);
            tpForceFullyEvolvedAtSlider.setValue(tpForceFullyEvolvedAtSlider.getMinimum());
        }

        if (tpPercentageLevelModifierCheckBox.isSelected()) {
            tpPercentageLevelModifierSlider.setEnabled(true);
        } else {
            tpPercentageLevelModifierSlider.setEnabled(false);
            tpPercentageLevelModifierSlider.setValue(0);
        }

        if (tpBossTrainersCheckBox.isSelected()) {
            tpBossTrainersSpinner.setEnabled(true);
        } else {
            tpBossTrainersSpinner.setEnabled(false);
            tpBossTrainersSpinner.setValue(1);
        }

        if (tpImportantTrainersCheckBox.isSelected()) {
            tpImportantTrainersSpinner.setEnabled(true);
        } else {
            tpImportantTrainersSpinner.setEnabled(false);
            tpImportantTrainersSpinner.setValue(1);
        }

        if (tpRegularTrainersCheckBox.isSelected()) {
            tpRegularTrainersSpinner.setEnabled(true);
        } else {
            tpRegularTrainersSpinner.setEnabled(false);
            tpRegularTrainersSpinner.setValue(1);
        }

        if (tpBossTrainersItemsCheckBox.isSelected() || tpImportantTrainersItemsCheckBox.isSelected() ||
                tpRegularTrainersItemsCheckBox.isSelected()) {
            tpConsumableItemsOnlyCheckBox.setEnabled(true);
            tpScriptedHeldItemsCheckBox.setEnabled(true);;
            tpSensibleItemsCheckBox.setEnabled(true);
            tpHighestLevelGetsItemCheckBox.setEnabled(true);
        } else {
            tpConsumableItemsOnlyCheckBox.setEnabled(false);
            tpScriptedHeldItemsCheckBox.setEnabled(false);
            tpSensibleItemsCheckBox.setEnabled(false);
            tpHighestLevelGetsItemCheckBox.setEnabled(false);
        }

        if (!spUnchangedRadioButton.isSelected() || !isTrainerSetting(TRAINER_UNCHANGED)) {
            tpRivalCarriesStarterCheckBox.setEnabled(true);
        } else {
            tpRivalCarriesStarterCheckBox.setEnabled(false);
            tpRivalCarriesStarterCheckBox.setSelected(false);
        }

        if (isTrainerSetting(TRAINER_TYPE_THEMED)) {
            tpWeightTypesCheckBox.setEnabled(true);
        } else {
            tpWeightTypesCheckBox.setEnabled(false);
            tpWeightTypesCheckBox.setSelected(false);
        }

        //trying to use similar strength is not compatible with scripting
        //since it would interfere with the concept of custom selection in an intrusive way (unlike things like pokemon restrictions and level modifiers)
        //it could still be implemented within the script by the user themselves
        //the same goes for "no early wonder guard", since the script can change both the level and pokemon and choose not to pick a random pokemon there is no way to enforce this rule
        if(isTrainerSetting(TRAINER_SCRIPTED))
        {
            tpSimilarStrengthCheckBox.setEnabled(false);
            tpSimilarStrengthCheckBox.setSelected(false);
            tpNoEarlyWonderGuardCheckBox.setEnabled(false);
            tpNoEarlyWonderGuardCheckBox.setSelected(false);
        }

        if (tpEliteFourUniquePokemonCheckBox.isSelected()) {
            tpEliteFourUniquePokemonSpinner.setEnabled(true);
        } else {
            tpEliteFourUniquePokemonSpinner.setEnabled(false);
            tpEliteFourUniquePokemonSpinner.setValue(1);
        }

        if (!totpUnchangedRadioButton.isSelected() || !totpAllyUnchangedRadioButton.isSelected()) {
            totpAllowAltFormesCheckBox.setEnabled(true);
        } else {
            totpAllowAltFormesCheckBox.setEnabled(false);
            totpAllowAltFormesCheckBox.setSelected(false);
        }

        if (totpPercentageLevelModifierCheckBox.isSelected()) {
            totpPercentageLevelModifierSlider.setEnabled(true);
        } else {
            totpPercentageLevelModifierSlider.setEnabled(false);
            totpPercentageLevelModifierSlider.setValue(0);
        }

        if (wpRandomRadioButton.isSelected()) {
            wpARNoneRadioButton.setEnabled(true);
            wpARSimilarStrengthRadioButton.setEnabled(true);
            wpARCatchEmAllModeRadioButton.setEnabled(true);
            wpARTypeThemeAreasRadioButton.setEnabled(true);
            wpBalanceShakingGrassPokemonCheckBox.setEnabled(true);
        } else if (wpArea1To1RadioButton.isSelected()) {
            wpARNoneRadioButton.setEnabled(true);
            wpARSimilarStrengthRadioButton.setEnabled(true);
            wpARCatchEmAllModeRadioButton.setEnabled(true);
            wpARTypeThemeAreasRadioButton.setEnabled(true);
            wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        } else if (wpGlobal1To1RadioButton.isSelected()) {
            if (wpARCatchEmAllModeRadioButton.isSelected() || wpARTypeThemeAreasRadioButton.isSelected()) {
                wpARNoneRadioButton.setSelected(true);
            }
            wpARNoneRadioButton.setEnabled(true);
            wpARSimilarStrengthRadioButton.setEnabled(true);
            wpARCatchEmAllModeRadioButton.setEnabled(false);
            wpARTypeThemeAreasRadioButton.setEnabled(false);
            wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        } else if(wpScriptedRadioButton.isSelected()) {
                wpARNoneRadioButton.setEnabled(false);
                wpARNoneRadioButton.setSelected(true);
                wpARSimilarStrengthRadioButton.setEnabled(false);
                wpARSimilarStrengthRadioButton.setSelected(false);
                wpARCatchEmAllModeRadioButton.setEnabled(false);
                wpARCatchEmAllModeRadioButton.setSelected(false);
                wpARTypeThemeAreasRadioButton.setEnabled(false);
                wpARTypeThemeAreasRadioButton.setSelected(false);
                wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        } else {
            wpARNoneRadioButton.setEnabled(false);
            wpARNoneRadioButton.setSelected(true);
            wpARSimilarStrengthRadioButton.setEnabled(false);
            wpARCatchEmAllModeRadioButton.setEnabled(false);
            wpARTypeThemeAreasRadioButton.setEnabled(false);
            wpBalanceShakingGrassPokemonCheckBox.setEnabled(false);
        }

        if (wpUnchangedRadioButton.isSelected()) {
            wpUseTimeBasedEncountersCheckBox.setEnabled(false);
            wpUseTimeBasedEncountersCheckBox.setSelected(false);
            wpDontUseLegendariesCheckBox.setEnabled(false);
            wpDontUseLegendariesCheckBox.setSelected(false);
            wpAllowAltFormesCheckBox.setEnabled(false);
            wpAllowAltFormesCheckBox.setSelected(false);
        } else {
            wpUseTimeBasedEncountersCheckBox.setEnabled(true);
            wpDontUseLegendariesCheckBox.setEnabled(true);
            wpAllowAltFormesCheckBox.setEnabled(true);
        }

        if (wpRandomizeHeldItemsCheckBox.isSelected()
                && wpRandomizeHeldItemsCheckBox.isVisible()
                && wpRandomizeHeldItemsCheckBox.isEnabled()) { // ??? why all three
            wpBanBadItemsCheckBox.setEnabled(true);
            wpScriptHeldItemsCheckBox.setEnabled(true);
        } else {
            wpBanBadItemsCheckBox.setEnabled(false);
            wpBanBadItemsCheckBox.setSelected(false);
            wpScriptHeldItemsCheckBox.setEnabled(false);
            wpScriptHeldItemsCheckBox.setSelected(false);
        }

        if (wpSetMinimumCatchRateCheckBox.isSelected()) {
            wpSetMinimumCatchRateSlider.setEnabled(true);
        } else {
            wpSetMinimumCatchRateSlider.setEnabled(false);
            wpSetMinimumCatchRateSlider.setValue(0);
        }

        if (wpPercentageLevelModifierCheckBox.isSelected()) {
            wpPercentageLevelModifierSlider.setEnabled(true);
        } else {
            wpPercentageLevelModifierSlider.setEnabled(false);
            wpPercentageLevelModifierSlider.setValue(0);
        }

        if (pmsMetronomeOnlyModeRadioButton.isSelected()) {
            tmUnchangedRadioButton.setEnabled(false);
            tmRandomRadioButton.setEnabled(false);
            tmScriptedRadioButton.setEnabled(false);
            tmUnchangedRadioButton.setSelected(true);

            mtUnchangedRadioButton.setEnabled(false);
            mtRandomRadioButton.setEnabled(false);
            mtScriptedRadioButton.setEnabled(false);
            mtUnchangedRadioButton.setSelected(true);

            tmLevelupMoveSanityCheckBox.setEnabled(false);
            tmLevelupMoveSanityCheckBox.setSelected(false);
            tmKeepFieldMoveTMsCheckBox.setEnabled(false);
            tmKeepFieldMoveTMsCheckBox.setSelected(false);
            tmForceGoodDamagingCheckBox.setEnabled(false);
            tmForceGoodDamagingCheckBox.setSelected(false);
            tmNoGameBreakingMovesCheckBox.setEnabled(false);
            tmNoGameBreakingMovesCheckBox.setSelected(false);
            tmFollowEvolutionsCheckBox.setEnabled(false);
            tmFollowEvolutionsCheckBox.setSelected(false);

            mtLevelupMoveSanityCheckBox.setEnabled(false);
            mtLevelupMoveSanityCheckBox.setSelected(false);
            mtKeepFieldMoveTutorsCheckBox.setEnabled(false);
            mtKeepFieldMoveTutorsCheckBox.setSelected(false);
            mtForceGoodDamagingCheckBox.setEnabled(false);
            mtForceGoodDamagingCheckBox.setSelected(false);
            mtNoGameBreakingMovesCheckBox.setEnabled(false);
            mtNoGameBreakingMovesCheckBox.setSelected(false);
            mtFollowEvolutionsCheckBox.setEnabled(false);
            mtFollowEvolutionsCheckBox.setSelected(false);
        } else {
            tmUnchangedRadioButton.setEnabled(true);
            tmRandomRadioButton.setEnabled(true);
            tmScriptedRadioButton.setEnabled(true);

            mtUnchangedRadioButton.setEnabled(true);
            mtRandomRadioButton.setEnabled(true);
            mtScriptedRadioButton.setEnabled(true);

            if (!(pmsUnchangedRadioButton.isSelected()) || !(tmUnchangedRadioButton.isSelected())
                    || !(thcUnchangedRadioButton.isSelected()) || thcScriptedRadioButton.isSelected()) {
                tmLevelupMoveSanityCheckBox.setEnabled(true);
            } else {
                tmLevelupMoveSanityCheckBox.setEnabled(false);
                tmLevelupMoveSanityCheckBox.setSelected(false);
            }

            if ((!thcUnchangedRadioButton.isSelected()) || (tmLevelupMoveSanityCheckBox.isSelected())) {
                tmFollowEvolutionsCheckBox.setEnabled(followEvolutionControlsEnabled);
            }
            else {
                tmFollowEvolutionsCheckBox.setEnabled(false);
                tmFollowEvolutionsCheckBox.setSelected(false);
            }

            if (!(tmUnchangedRadioButton.isSelected())) {
                tmKeepFieldMoveTMsCheckBox.setEnabled(true);
                tmForceGoodDamagingCheckBox.setEnabled(true);
                tmNoGameBreakingMovesCheckBox.setEnabled(true);
            } else {
                tmKeepFieldMoveTMsCheckBox.setEnabled(false);
                tmKeepFieldMoveTMsCheckBox.setSelected(false);
                tmForceGoodDamagingCheckBox.setEnabled(false);
                tmForceGoodDamagingCheckBox.setSelected(false);
                tmNoGameBreakingMovesCheckBox.setEnabled(false);
                tmNoGameBreakingMovesCheckBox.setSelected(false);
            }

            if (romHandler.hasMoveTutors()
                    && (!(pmsUnchangedRadioButton.isSelected()) || !(mtUnchangedRadioButton.isSelected())
                    || !(mtcUnchangedRadioButton.isSelected()))) {
                mtLevelupMoveSanityCheckBox.setEnabled(true);
            } else {
                mtLevelupMoveSanityCheckBox.setEnabled(false);
                mtLevelupMoveSanityCheckBox.setSelected(false);
            }

            if (!(mtcUnchangedRadioButton.isSelected()) || (mtLevelupMoveSanityCheckBox.isSelected())) {
                mtFollowEvolutionsCheckBox.setEnabled(followEvolutionControlsEnabled);
            }
            else {
                mtFollowEvolutionsCheckBox.setEnabled(false);
                mtFollowEvolutionsCheckBox.setSelected(false);
            }

            if (romHandler.hasMoveTutors() && !(mtUnchangedRadioButton.isSelected())) {
                mtKeepFieldMoveTutorsCheckBox.setEnabled(true);
                mtForceGoodDamagingCheckBox.setEnabled(true);
                mtNoGameBreakingMovesCheckBox.setEnabled(true);
            } else {
                mtKeepFieldMoveTutorsCheckBox.setEnabled(false);
                mtKeepFieldMoveTutorsCheckBox.setSelected(false);
                mtForceGoodDamagingCheckBox.setEnabled(false);
                mtForceGoodDamagingCheckBox.setSelected(false);
                mtNoGameBreakingMovesCheckBox.setEnabled(false);
                mtNoGameBreakingMovesCheckBox.setSelected(false);
            }
        }

        if (tmForceGoodDamagingCheckBox.isSelected()) {
            tmForceGoodDamagingSlider.setEnabled(true);
        } else {
            tmForceGoodDamagingSlider.setEnabled(false);
            tmForceGoodDamagingSlider.setValue(tmForceGoodDamagingSlider.getMinimum());
        }

        if (mtForceGoodDamagingCheckBox.isSelected()) {
            mtForceGoodDamagingSlider.setEnabled(true);
        } else {
            mtForceGoodDamagingSlider.setEnabled(false);
            mtForceGoodDamagingSlider.setValue(mtForceGoodDamagingSlider.getMinimum());
        }

        tmFullHMCompatibilityCheckBox.setEnabled(!thcFullCompatibilityRadioButton.isSelected());

        if (fiRandomRadioButton.isSelected() && fiRandomRadioButton.isVisible() && fiRandomRadioButton.isEnabled()) {
            fiBanBadItemsCheckBox.setEnabled(true);
        } else if (fiRandomEvenDistributionRadioButton.isSelected() && fiRandomEvenDistributionRadioButton.isVisible()
                && fiRandomEvenDistributionRadioButton.isEnabled()) {
            fiBanBadItemsCheckBox.setEnabled(true);
        } else if (fiScriptedRadioButton.isSelected() && fiScriptedRadioButton.isVisible()
                && fiScriptedRadioButton.isEnabled()) {
            fiBanBadItemsCheckBox.setEnabled(true);
        }  else {
            fiBanBadItemsCheckBox.setEnabled(false);
            fiBanBadItemsCheckBox.setSelected(false);
        }

        if (fiScriptedRadioButton.isSelected() && fiScriptedRadioButton.isVisible()
                && fiScriptedRadioButton.isEnabled()) {
            fiShuffleItemsCheckBox.setEnabled(true);
        }
        else{
            fiShuffleItemsCheckBox.setEnabled(false);
            fiShuffleItemsCheckBox.setSelected(false);
        }

        boolean randomShopItemsSelected = (shRandomRadioButton.isSelected() && shRandomRadioButton.isVisible() && shRandomRadioButton.isEnabled());
        boolean scriptedShopItemsSelected = (shScriptedRadioButton.isSelected() && shScriptedRadioButton.isVisible() && shScriptedRadioButton.isEnabled());
        if (randomShopItemsSelected || scriptedShopItemsSelected) {
            shBanBadItemsCheckBox.setEnabled(true);
            shBanRegularShopItemsCheckBox.setEnabled(true);
            shBanOverpoweredShopItemsCheckBox.setEnabled(true);
            if(shScriptedPricesCheckbox.isSelected()) {
                shBalanceShopItemPricesCheckBox.setEnabled(false);
                shBalanceShopItemPricesCheckBox.setSelected(false);
            }
            else {
                shBalanceShopItemPricesCheckBox.setEnabled(true);
            }
            shGuaranteeEvolutionItemsCheckBox.setEnabled(!scriptedShopItemsSelected);
            shGuaranteeXItemsCheckBox.setEnabled(!scriptedShopItemsSelected);
        } else {
            shBanBadItemsCheckBox.setEnabled(false);
            shBanBadItemsCheckBox.setSelected(false);
            shBanRegularShopItemsCheckBox.setEnabled(false);
            shBanRegularShopItemsCheckBox.setSelected(false);
            shBanOverpoweredShopItemsCheckBox.setEnabled(false);
            shBanOverpoweredShopItemsCheckBox.setSelected(false);
            shBalanceShopItemPricesCheckBox.setEnabled(false);
            shBalanceShopItemPricesCheckBox.setSelected(false);
            shGuaranteeEvolutionItemsCheckBox.setEnabled(false);
            shGuaranteeEvolutionItemsCheckBox.setSelected(false);
            shGuaranteeXItemsCheckBox.setEnabled(false);
            shGuaranteeXItemsCheckBox.setSelected(false);
        }

        if ((puRandomRadioButton.isSelected() && puRandomRadioButton.isVisible() && puRandomRadioButton.isEnabled()) ||
                (puScriptedRadioButton.isSelected() && puScriptedRadioButton.isVisible() && puScriptedRadioButton.isEnabled())) {
            puBanBadItemsCheckBox.setEnabled(true);
        } else {
            puBanBadItemsCheckBox.setEnabled(false);
            puBanBadItemsCheckBox.setSelected(false);
        }
    }

    private void initTweaksPanel() {
        tweakCheckBoxes = new ArrayList<>();
        int numTweaks = MiscTweak.allTweaks.size();
        for (int i = 0; i < numTweaks; i++) {
            MiscTweak ct = MiscTweak.allTweaks.get(i);
            JCheckBox tweakBox = new JCheckBox();
            tweakBox.setText(ct.getTweakName());
            tweakBox.setToolTipText(ct.getTooltipText());
            tweakCheckBoxes.add(tweakBox);
        }
    }

    private void makeTweaksLayout(List<JCheckBox> tweaks) {
        liveTweaksPanel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Misc. Tweaks");
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD));
        liveTweaksPanel.setBorder(border);

        int numTweaks = tweaks.size();
        Iterator<JCheckBox> tweaksIterator = tweaks.iterator();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(5,5,0,5);

        int TWEAK_COLS = 4;
        int numCols = Math.min(TWEAK_COLS, numTweaks);

        for (int row = 0; row <= numTweaks / numCols; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!tweaksIterator.hasNext()) break;
                c.gridx = col;
                c.gridy = row;
                liveTweaksPanel.add(tweaksIterator.next(),c);
            }
        }

        // Pack the checkboxes together

        GridBagConstraints horizontalC = new GridBagConstraints();
        horizontalC.gridx = numCols;
        horizontalC.gridy = 0;
        horizontalC.weightx = 0.1;

        GridBagConstraints verticalC = new GridBagConstraints();
        verticalC.gridx = 0;
        verticalC.gridy = (numTweaks / numCols) + 1;
        verticalC.weighty = 0.1;

        liveTweaksPanel.add(new JSeparator(SwingConstants.HORIZONTAL),horizontalC);
        liveTweaksPanel.add(new JSeparator(SwingConstants.VERTICAL),verticalC);
    }

    private void populateDropdowns() {
        List<Pokemon> currentStarters = romHandler.getStarters();
        List<Pokemon> allPokes =
                romHandler.generationOfPokemon() >= 6 ?
                        romHandler.getPokemonInclFormes()
                                .stream()
                                .filter(pk -> pk == null || !pk.actuallyCosmetic)
                                .collect(Collectors.toList()) :
                        romHandler.getPokemon();
        String[] pokeNames = new String[allPokes.size()];
        pokeNames[0] = "Random";
        for (int i = 1; i < allPokes.size(); i++) {
            pokeNames[i] = allPokes.get(i).fullName();

        }

        spComboBox1.setModel(new DefaultComboBoxModel<>(pokeNames));
        spComboBox1.setSelectedIndex(allPokes.indexOf(currentStarters.get(0)));
        spComboBox2.setModel(new DefaultComboBoxModel<>(pokeNames));
        spComboBox2.setSelectedIndex(allPokes.indexOf(currentStarters.get(1)));
        if (!romHandler.isYellow()) {
            spComboBox3.setModel(new DefaultComboBoxModel<>(pokeNames));
            spComboBox3.setSelectedIndex(allPokes.indexOf(currentStarters.get(2)));
        }

        String[] baseStatGenerationNumbers = new String[Math.min(3, GlobalConstants.HIGHEST_POKEMON_GEN - romHandler.generationOfPokemon())];
        int j = Math.max(6,romHandler.generationOfPokemon() + 1);
        for (int i = 0; i < baseStatGenerationNumbers.length; i++) {
            baseStatGenerationNumbers[i] = String.valueOf(j);
            j++;
        }
        pbsUpdateComboBox.setModel(new DefaultComboBoxModel<>(baseStatGenerationNumbers));

        String[] moveGenerationNumbers = new String[GlobalConstants.HIGHEST_POKEMON_GEN - romHandler.generationOfPokemon()];
        j = romHandler.generationOfPokemon() + 1;
        for (int i = 0; i < moveGenerationNumbers.length; i++) {
            moveGenerationNumbers[i] = String.valueOf(j);
            j++;
        }
        mdUpdateComboBox.setModel(new DefaultComboBoxModel<>(moveGenerationNumbers));


        tpComboBox.setModel(new DefaultComboBoxModel<>(getTrainerSettingsForGeneration(romHandler.generationOfPokemon())));
        tpComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent comp = (JComponent) super.getListCellRendererComponent(list,
                        value, index, isSelected, cellHasFocus);

                if (index >= 0 && value != null) {
                    list.setToolTipText(bundle.getString(trainerSettingToolTips.get(trainerSettings.indexOf(value))));
                }
                return comp;
            }
        });
    }

    private ImageIcon makeMascotIcon() {
        try {
            BufferedImage handlerImg = romHandler.getMascotImage();

            if (handlerImg == null) {
                return emptyIcon;
            }

            BufferedImage nImg = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            int hW = handlerImg.getWidth();
            int hH = handlerImg.getHeight();
            nImg.getGraphics().drawImage(handlerImg, 64 - hW / 2, 64 - hH / 2, frame);
            return new ImageIcon(nImg);
        } catch (Exception ex) {
            return emptyIcon;
        }
    }

    private void checkCustomNames() {
        String[] cnamefiles = new String[] { SysConstants.tnamesFile, SysConstants.tclassesFile,
                SysConstants.nnamesFile };

        boolean foundFile = false;
        for (int file = 0; file < 3; file++) {
            File currentFile = new File(SysConstants.ROOT_PATH + cnamefiles[file]);
            if (currentFile.exists()) {
                foundFile = true;
                break;
            }
        }

        if (foundFile) {
            int response = JOptionPane.showConfirmDialog(frame,
                    bundle.getString("GUI.convertNameFilesDialog.text"),
                    bundle.getString("GUI.convertNameFilesDialog.title"), JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    CustomNamesSet newNamesData = CustomNamesSet.importOldNames();
                    byte[] data = newNamesData.getBytes();
                    FileFunctions.writeBytesToFile(SysConstants.customNamesFile, data);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, bundle.getString("GUI.convertNameFilesFailed"));
                }
            }

            haveCheckedCustomNames = true;
            attemptWriteConfig();
        }

    }

    private void attemptReadConfig() {
        // Things that should be true by default should be manually set here
        unloadGameOnSuccess = true;
        File fh = new File(SysConstants.ROOT_PATH + "config.ini");
        if (!fh.exists() || !fh.canRead()) {
            return;
        }

        try {
            Scanner sc = new Scanner(fh, "UTF-8");
            boolean isReadingUpdates = false;
            while (sc.hasNextLine()) {
                String q = sc.nextLine().trim();
                if (q.contains("//")) {
                    q = q.substring(0, q.indexOf("//")).trim();
                }
                if (q.equals("[Game Updates]")) {
                    isReadingUpdates = true;
                    continue;
                }
                if (!q.isEmpty()) {
                    String[] tokens = q.split("=", 2);
                    if (tokens.length == 2) {
                        String key = tokens[0].trim();
                        if (isReadingUpdates) {
                            gameUpdates.put(key, tokens[1]);
                        }
                        if (key.equalsIgnoreCase("checkedcustomnames172")) {
                            haveCheckedCustomNames = Boolean.parseBoolean(tokens[1].trim());
                        }
                        if (key.equals("firststart")) {
                            String val = tokens[1];
                            if (val.equals(Version.VERSION_STRING)) {
                                initialPopup = false;
                            }
                        }
                        if (key.equals("unloadgameonsuccess")) {
                            unloadGameOnSuccess = Boolean.parseBoolean(tokens[1].trim());
                        }
                        if (key.equals("showinvalidrompopup")) {
                            showInvalidRomPopup = Boolean.parseBoolean(tokens[1].trim());
                        }
                    }
                } else if (isReadingUpdates) {
                    isReadingUpdates = false;
                }
            }
            sc.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean attemptWriteConfig() {
        File fh = new File(SysConstants.ROOT_PATH + "config.ini");
        if (fh.exists() && !fh.canWrite()) {
            return false;
        }

        try {
            PrintStream ps = new PrintStream(new FileOutputStream(fh), true, "UTF-8");
            ps.println("checkedcustomnames=true");
            ps.println("checkedcustomnames172=" + haveCheckedCustomNames);
            ps.println("unloadgameonsuccess=" + unloadGameOnSuccess);
            ps.println("showinvalidrompopup=" + showInvalidRomPopup);
            if (!initialPopup) {
                ps.println("firststart=" + Version.VERSION_STRING);
            }
            if (gameUpdates.size() > 0) {
                ps.println();
                ps.println("[Game Updates]");
                for (Map.Entry<String, String> update : gameUpdates.entrySet()) {
                    ps.format("%s=%s", update.getKey(), update.getValue());
                    ps.println();
                }
            }
            ps.close();
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    private void testForRequiredConfigs() {
        try {
            Utils.testForRequiredConfigs();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    String.format(bundle.getString("GUI.configFileMissing"), e.getMessage()));
            System.exit(1);
        }
    }

    private ExpCurve[] getEXPCurvesForGeneration(int generation) {
        ExpCurve[] result;
        if (generation < 3) {
            result = new ExpCurve[]{ ExpCurve.MEDIUM_FAST, ExpCurve.MEDIUM_SLOW, ExpCurve.FAST, ExpCurve.SLOW };
        } else {
            result = new ExpCurve[]{ ExpCurve.MEDIUM_FAST, ExpCurve.MEDIUM_SLOW, ExpCurve.FAST, ExpCurve.SLOW, ExpCurve.ERRATIC, ExpCurve.FLUCTUATING };
        }
        return result;
    }

    private String[] getTrainerSettingsForGeneration(int generation) {
        List<String> result = new ArrayList<>(trainerSettings);
        if (generation != 5) {
            result.remove(bundle.getString("GUI.tpMain3RandomEvenDistributionMainGame.text"));
        }
        return result.toArray(new String[0]);
    }

    private boolean isTrainerSetting(int setting) {
        return trainerSettings.indexOf(tpComboBox.getSelectedItem()) == setting;
    }

    private String addImport(String sourceStr, String importFrom, String imported)
    {
        String importStr = "from " + importFrom + " import " + imported + "\n";
        if(!sourceStr.contains(importStr))
        {
            sourceStr = importStr + sourceStr;
        }
        return sourceStr;
    }

    private String addExampleFunc(String sourceStr, String funcDeclaration, String[] topComments, String exampleBody)
    {
        if(!sourceStr.contains(funcDeclaration))
        {
            String exampleFunc = "\n";
            for(String comm : topComments)
            {
                exampleFunc += comm + "\n";
            }
            exampleFunc += funcDeclaration + exampleBody;
            sourceStr += "\n" + exampleFunc;
        }
        return sourceStr;
    }

    private void openConsoleWindow()
    {
        consoleWindow.setVisible(true);
        consoleWindow.requestFocus();
    }

    private boolean anyScripted()
    {
        return
                stpScriptedRadioButton.isSelected() ||
                igtScriptedRadioButton.isSelected() ||
                wpScriptedRadioButton.isSelected() ||
                tpScriptedHeldItemsCheckBox.isSelected() ||
                limitPokemonScriptingCheckbox.isSelected() ||
                wpScriptHeldItemsCheckBox.isSelected() ||
                mdScriptedCheckBox.isSelected() ||
                pmsScriptLearntCheckBox.isSelected() ||
                pmsScriptEggCheckBox.isSelected() ||
                pmsScriptLearnAfterCheckBox.isSelected() ||
                pbsScriptedRadioButton.isSelected() ||
                ptScriptedRadioButton.isSelected() ||
                paScriptedRadioButton.isSelected() ||
                pbsScriptedEXPCurveRadioButton.isSelected() ||
                fiScriptedRadioButton.isSelected() ||
                puScriptedRadioButton.isSelected() ||
                thcScriptedRadioButton.isSelected() ||
                tmScriptedRadioButton.isSelected() ||
                mtcScriptedRadioButton.isSelected() ||
                mtScriptedRadioButton.isSelected() ||
                spScriptedRadioButton.isSelected()
        ;
    }

    private boolean resetOnFail()
    {
        boolean script = anyScripted();
        return !script;
    }

    private void addStarterScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects starter pokemon",
                "#  pokepool - an array of Pokemon objects representing all available pokemon",
                "#  starterIndex - The index of the current starter [0 - 2]",
                "#",
                "#  return: a Pokemon object that is present in pokepool"
        };
        String funcBody = "\n\treturn pokepool[starterIndex] #example";
        String funcDeclaration = "def selectStarter(pokepool, starterIndex):";

        if(spScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    private void addStaticScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects all static pokemon settings",
                "#pokepool - an array of Pokemon objects representing all available pokemon",
                "#           (some encounters might have a different restricted pool)",
                "#oldEncounter - a StaticEncounter object representing the original encounter",
                "#megaSwap - true if the current request is for swapping a mega-evolvable pokemon",
                "#           (if true, the pokepool will only contain mega-evolvable pokemon that have not been selected before)",
                "#           (if true, the held item of the pokemon will be changed to its mega stone)",
                "#",
                "#return: a StaticEncounter object representing the modified encounter"
        };
        String funcDeclaration = "def selectStaticPokemon(pokepool, oldEncounter, megaSwap):";
        String funcBody = "\n\toldEncounter.level = min(100, oldEncounter.level + 30) #example\n\treturn oldEncounter";

        if(stpScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "StaticEncounter");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    private void addTradeScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects given and/or received pokemon in in-game trades",
                "#pokepool - an array of Pokemon objects representing all available pokemon",
                "#oldTrade - an IngameTrade object representing the original trade",
                "#",
                "#return: an IngameTrade object representing the modified trade",
                "#NOTE: Nicknames, OTs, IVs, and items can be set here, but will be overridden by the unscripted options if their checkboxes are selected"
        };
        String funcDeclaration = "def selectInGameTradePokemon(pokepool, oldTrade):";
        String funcBody = "\n\toldTrade.givenPokemon = pokepool[0] #example\n\toldTrade.nickname = \"Dippy\"\n\treturn oldTrade";

        if(igtScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "IngameTrade");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addWildEncounterScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects wild pokemon encounters per-area",
                "#pokepool - an array of Pokemon objects representing all available pokemon in this current area",
                "#oldArea - an EncounterSet object representing the original area of wild pokemon",
                "#",
                "#return: an EncounterSet object representing the modified area of wild pokemon"
        };
        String funcDeclaration = "def selectWildEncountersForArea(pokepool, oldArea):";
        String funcBody = "\n\tfor enc in oldArea.encounters: #example\n\t\tenc.pokemon = pokepool[min(enc.level, len(pokepool) - 1)]\n\treturn oldArea";

        if(wpScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "EncounterSet");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Encounter");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addTrainerScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects the pokemon of the given trainer",
                "#pokepool - an array of Pokemon objects representing all available pokemon in this current area",
                "#trainer - a Trainer object representing the trainer that owns the pokemon",
                "#oldPokemon - a TrainerPokemon object representing the original pokemon",
                "#megaSwap - true if the current request is for swapping a mega-evolvable pokemon",
                "#           (if true, the pokepool will only contain mega-evolvable pokemon that have not been selected before)",
                "#           (if true, the held item of the pokemon will be changed to its mega stone)",
                "#",
                "#return: a TrainerPokemon object representing the modified pokemon",
                "#NOTE: the result of this function can be overridden if \"rival carries starter\" or \"force fully evolved\" options are used",
                "#NOTE: the additional pokemon option will already be applied, so those pokemon are selected here as well"
        };
        String funcDeclaration = "def selectTrainerPokemon(pokepool, trainer, oldPokemon, megaSwap):";
        String funcBody = "\n\toldPokemon.pokemon = pokepool[min(oldPokemon.level, len(pokepool) - 1)] #example\n\treturn oldPokemon";

        if(isTrainerSetting(TRAINER_SCRIPTED))
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Trainer");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "TrainerPokemon");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addTrainerHeldItemScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects the held item for the given trainer pokemon",
                "#itempool - an array of integers representing all available held items",
                "#           (this is affected by the \"Consumable only\" and \"Sensible items\" settings)",
                "#trainer - a Trainer object representing the trainer that owns the pokemon to give an item to",
                "#pokemon - a TrainerPokemon object representing the pokemon to give an item to",
                "#",
                "#return: an integer representing the index of the new held item or -1 if there should be no held item",
                "#NOTE: use the imported Item class to access held items by variable name"
        };
        String funcDeclaration = "def selectTrainerPokemonItem(itempool, trainer, pokemon):";
        String funcBody = "\n\tif(Items.oranBerry in itempool): #example\n\t\treturn Items.oranBerry\n\treturn 0";

        if(tpScriptedHeldItemsCheckBox.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Items");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Trainer");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "TrainerPokemon");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addPokemonLimitScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#filters the given list of pokemon to limit the pokemon available in-game (this affects all options where pokemon are selected)",
                "#pokepool - an array of Pokemon objects representing all available pokemon before this function is called",
                "#           (this is affected by the usual pokemon limiting options)",
                "#",
                "#return: an array of Pokemon objects representing all available pokemon"
        };
        String funcDeclaration = "def limitPokemon(pokepool):";
        String funcBody = "\n\tpokepool = [poke for poke in pokepool if 'B' in poke.name] #example\n\treturn pokepool";

        if(limitPokemonScriptingCheckbox.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addWildHeldItemScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#filters the given list of pokemon to limit the pokemon available in-game (this affects all options where pokemon are selected)",
                "#itempool - an array of integers representing all available items",
                "#pokemon - the pokemon to set the held item(s) for",
                "#supportCommon - true if this pokemon can have a common held item (if set when false it will be ignored)",
                "#supportRare - true if this pokemon can have a rare held item (if set when false it will be ignored)",
                "#supportGuaranteed - true if this pokemon can have a guaranteed held item (if set when false it will be ignored)",
                "#supportDarkGrass - true if this pokemon can have a dark grass held item (if set when false it will be ignored)",
                "#",
                "#return:   a dictionary of structure: { \"common\": int, \"rare\": int, \"guaranteed\": int, \"darkGrass\": int }, missing keys are ignored",
                "#          that represents the held items the pokemon can have in the wild (with -1 being none and 0 being unchanged)",
                "#NOTE: use the imported Item class to access held items by variable name"

        };
        String funcDeclaration = "def selectWildPokemonHeldItem(itempool, pokemon, supportCommon, supportRare, supportGuaranteed, supportDarkGrass):";
        String funcBody = "\n\tresult = { \"common\": 0, \"rare\": 0, \"guaranteed\": 0, \"darkGrass\": 0 }\n\tif pokemon.name == 'BULBASAUR':\n\t\tresult[\"guaranteed\"] = Items.masterBall\n\treturn result";

        if(wpScriptHeldItemsCheckBox.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Items");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addMoveDataScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Sets the move data of the given move",
                "#oldMove - a Move object representing the original move",
                "#hasPhysicalSpecialSplit - True if the game being randomized for has a physical-special split (true for generation 4+)",
                "#",
                "#return: a Move object representing the modified move",
                "#NOTE: use the imported Move class to access moves by variable name",
                "#NOTE: the result of this function will still be affected by other selected move data options"
        };
        String funcDeclaration = "def setMoveData(oldMove, hasPhysicalSpecialSplit):";
        String funcBody = "\n\tif(oldMove.category == MoveCategory.PHYSICAL): #example\n\t\toldMove.power = max(10, 100 - oldMove.power)\n\toldMove.pp *= 2\n\treturn oldMove";

        if(mdScriptedCheckBox.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Move");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "MoveCategory");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addLearntMovesScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies a pokemon's learnt moves BEFORE other options are run on it",
                "#movepool - an array of Move objects representing all available moves",
                "#pokemon - a Pokemon object representing the pokemon whose moveset is being changed",
                "#oldMoveset - an array of MoveLearnt objects representing the original set of moves learned by leveling up",
                "#",
                "#return: an array of MoveLearnt objects representing the modified set of moves learned by leveling up",
                "#NOTE: use the imported Move class to access moves by variable name",
                "#NOTE: the result of this function will still be affected by other selected moveset options"
        };
        String funcDeclaration = "def setLearntMoveset(movepool, pokemon, oldMoveset):";
        String funcBody = "\n\tnewMove = MoveLearnt() #example\n\tnewMove.move = Moves.splash\n\tnewMove.level = 100\n\toldMoveset.append(newMove)\n\treturn oldMoveset";

        if(pmsScriptLearntCheckBox.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "MoveLearnt");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addAfterLearntMovesScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies a pokemon's learnt moves AFTER other options are run on it",
                "#movepool - an array of Move objects representing all available moves",
                "#pokemon - a Pokemon object representing the pokemon whose moveset is being changed",
                "#oldMoveset - an array of MoveLearnt objects representing the original set of moves learned by leveling up",
                "#",
                "#return: an array of MoveLearnt objects representing the modified set of moves learned by leveling up",
                "#NOTE: use the imported Move class to access moves by variable name",
                "#NOTE: the result of this function will not be affected by other moveset options because they have been applied beforehand"
        };
        String funcDeclaration = "def setLearntMovesetPost(movepool, pokemon, oldMoveset):";
        String funcBody = "\n\tfor x in oldMoveset: #example\n\t\tx.move = Moves.splash\n\treturn oldMoveset";

        if(pmsScriptLearnAfterCheckBox.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "MoveLearnt");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addEggMovesScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies a pokemon's egg moves AFTER other options are run on it",
                "#movepool - an array of Move objects representing all available moves",
                "#pokemon - a Pokemon object representing the pokemon whose moveset is being changed",
                "#oldMoveset - an array of Move objects representing the original set of egg moves",
                "#",
                "#return: an array of Move objects representing the modified set of egg moves",
                "#NOTE: use the imported Move class to access moves by variable name",
                "#NOTE: the result of this function will still be affected by other selected moveset options",
                "#WARNING: you CANNOT change the number of egg moves a pokemon has, you can only change what moves they are"
        };
        String funcDeclaration = "def setEggMoveset(movepool, pokemon, oldMoveset):";
        String funcBody = "\n\tif(len(oldMoveset) > 0): #example\n\t\toldMoveset[0] = find(movepool, Moves.splash)\n\treturn oldMoveset";

        if(pmsScriptEggCheckBox.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addBaseStatsScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies a pokemon's base stats",
                "#pokemon - a Pokemon object representing the pokemon whose base stats are being changed",
                "#",
                "#return: a dictionary of structure: { \"hp\": int, \"atk\": int, \"def\": int, \"spatk\": int, \"spdef\": int, \"spd\": int }, missing keys are ignored",
                "#NOTE: if options for carrying base stats over to (mega-)evolutions are selected, only base pokemon will be put through this function",
                "#NOTE: the \"Update Base Stats to Generation\" option is applied before this function is run"
        };
        String funcDeclaration = "def setBaseStats(pokemon):";
        String funcBody = "\n\tresult = { \"hp\": 5, \"atk\": pokemon.attack / 2 }\n\treturn result";

        if(pbsScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addPokemonTypeScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies a pokemon's type(s)",
                "#pokemon - a Pokemon object representing the pokemon whose type(s) are being changed",
                "#",
                "#return: a dictionary of structure: { \"primary\": Type, \"secondary\": Type }, missing keys are ignored",
                "#NOTE: you can explicitly set the secondary type to None, if you set the primary type to None it will be ignored"
        };
        String funcDeclaration = "def selectPokemonTypes(pokemon):";
        String funcBody = "\n\tresult = { \"primary\": Type.NORMAL, \"secondary\": Type.BUG }\n\treturn result";

        if(ptScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Type");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addPokemonAbilityScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies a pokemon's abilities",
                "#pokemon - a Pokemon object representing the pokemon whose abilities are being changed",
                "#abilitypool - an array of integers representing all available abilities",
                "#abilityCount - the number of abilities to select (any extra will be ignored)",
                "#",
                "#return: an array of ability indices",
                "#NOTE: you can access abilities through the imported Abilities class"
        };
        String funcDeclaration = "def selectPokemonAbilities(pokemon, abilitypool, maxAbilities):";
        String funcBody = "\n\tresult = [Abilities.sturdy, Abilities.speedBoost, Abilities.static] #example\n\tfor index, ability in enumerate(result):\n\t\tif(not ability in abilitypool):\n\t\t\tresult[index] = 0\n\treturn result";

        if(paScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Abilities");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addPokemonEXPCurveScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies a pokemon's EXP curve",
                "#pokemon - a Pokemon object representing the pokemon whose EXP curve is being changed",
                "#",
                "#return: an ExpCurve object representing the new ExpCurve of the pokemon",
                "#NOTE: you can access ExpCurves through the imported ExpCurve class"
        };
        String funcDeclaration = "def selectPokemonEXPCurve(pokemon):";
        String funcBody = "\n\tif(pokemon.number % 2 == 0):\n\t\treturn ExpCurve.SLOW\n\telse:\n\t\treturn ExpCurve.FAST";

        if(pbsScriptedEXPCurveRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "ExpCurve");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addFieldItemScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies field items found in the overworld",
                "#oldItem - an Integer representing the original field item",
                "#itemPool - an array of integers representing all available items",
                "#isTM - True if the current field item is a TM, false otherwise",
                "#",
                "#return: an integer representing the new field item",
                "#NOTE: you can access items through the imported Items class",
                "#NOTE: when isTM is true, the itemPool will only contain TMs, otherwise it will contain every other item",
                "#NOTE: some items are considered unique and will be removed from the itempool for the next function call if selected",
                "#NOTE: TMs will always be shuffled afer this function is called to include some required TMs. Field items will only be shuffled if the option for it is selected"
        };
        String funcDeclaration = "def selectFieldItem(oldItem, itemPool, isTM):";
        String funcBody = "\n\tif(isTM): #example\n\t\treturn Items.tm01\n\telse:\n\t\treturn Items.potion";

        if(fiScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Items");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addPickupItemScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Modifies items gained from the pickup ability",
                "#oldItem - an Integer representing the original pickup item",
                "#itemPool - an array of integers representing all available items",
                "#",
                "#return: an integer representing the new pickup item",
                "#NOTE: you can access items through the imported Items class",
                "#NOTE: in some games (gen 3 & 4) TMs can be used as pickup items and they will be included in the itempool for those games"
        };
        String funcDeclaration = "def selectPickupItem(oldItem, itemPool):";
        String funcBody = "\n\tif(oldItem + 1 in itemPool): #example\n\t\treturn oldItem + 1\n\telse:\n\t\treturn oldItem";

        if(puScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Items");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addStarterHeldItemScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Selects held items given to starters",
                "#oldItem - an int representing the original starter held item",
                "#itemPool - an array of integers representing all available items",
                "#",
                "#return: an int representing the new starter held item",
                "#NOTE: you can access items through the imported Items class"
        };
        String funcDeclaration = "def selectStarterHeldItem(oldItem, itemPool):";
        String funcBody = "\n\tif(oldItem + 1 in itemPool): #example\n\t\treturn oldItem + 1\n\treturn oldItem";

        if(spScriptedStarterHeldItemsRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Items");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addTMScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects a move for a TM",
                "#oldMove - a Move object representing the move that is being replaced",
                "#movepool - an array of Move objects representing all available moves",
                "#forcedDamaging - True if the current move is forced to be a good damaging move",
                "#",
                "#return: a Move object representing the new move",
                "#NOTE: use the imported Moves class to access move numbers by name",
                "#NOTE: forcedDamaging is only used when \"Force % of good damaging moves\" is used, when True the movepool will only contain good damaging moves"
        };
        String funcDeclaration = "def selectTMMove(oldMove, movepool, forcedDamaging):";
        String funcBody = "\n\treturn movepool[len(movepool) / 2] #example";

        if(tmScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Move");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addTMCompatFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#returns true if the given pokemon should be able to learn the given TM move",
                "#move - a Move object representing the move to learn from the TM",
                "#pokemon - a Pokemon object representing the pokemon that would potentially be able to learn the move",
                "#",
                "#return: true if the pokemon should be able to learn the given move"
        };
        String funcDeclaration = "def selectTMCompatibility(move, pokemon):";
        String funcBody = "\n\tisPrimaryType = move.type == pokemon.primaryType #example\n\tisSecondaryType = pokemon.secondaryType is not None and move.type == pokemon.secondaryType\n\treturn isPrimaryType or isSecondaryType or move.type == Type.NORMAL";

        if(thcScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Move");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Type");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addTutorCompatFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#returns true if the given pokemon should be able to learn the given tutor move",
                "#move - a Move object representing the move to learn from the tutor",
                "#pokemon - a Pokemon object representing the pokemon that would potentially be able to learn the move",
                "#",
                "#return: true if the pokemon should be able to learn the given move"
        };
        String funcDeclaration = "def selectTutorCompatibility(move, pokemon):";
        String funcBody = "\n\tisPrimaryType = move.type == pokemon.primaryType #example\n\tisSecondaryType = pokemon.secondaryType is not None and move.type == pokemon.secondaryType\n\treturn isPrimaryType or isSecondaryType or move.type == Type.NORMAL";

        if(mtcScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Move");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Type");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addTutorScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects a move for a move tutor",
                "#oldMove - a Move object representing the move that is being replaced",
                "#movepool - an array of Move objects representing all available moves",
                "#forcedDamaging - True if the current move is forced to be a good damaging move",
                "#",
                "#return: a Move object representing the new move",
                "#NOTE: use the imported Moves class to access move numbers by name",
                "#NOTE: forcedDamaging is only used when \"Force % of good damaging moves\" is used, when True the movepool will only contain good damaging moves"
        };
        String funcDeclaration = "def selectTutorMove(oldMove, movepool, forcedDamaging):";
        String funcBody = "\n\treturn movepool[len(movepool) / 2] #example";

        if(mtScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Move");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    private void addEvolutionScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#selects evolutions for the given pokemon",
                "#  pokepool - an array of Pokemon objects representing all pokemon you can choose for this evolution",
                "#  poke - a Pokemon object representing the pokemon to pick evolutions for",
                "#  oldEvos - an array of Evolution dictionaries representing the original evolutions of this pokemon",
                "#",
                "#  return: a Sequence of Evolution dictionaries representing the new evolutions for the given pokemon",
                "#  NOTE: Evolution dictionaries have the following structure: { \"evolveTo\": Pokemon, \"type\": EvolutionType }",
                "#        If the evolution type uses levels,  the \"level\" : int field is required, the field should be the required level",
                "#        If the evolution type uses moves,   the \"move\"  : int field is required, the field should be the index of the required move (attainable from the Move class)",
                "#        If the evolution type uses items,   the \"item\"  : int field is required, the field should be the index of the required item (attainable from the Items class)",
                "#        If the evolution type uses species, the \"species\" : int field is required, the field should be the index of the required pokemon (attainable from the Species class)"
        };
        String funcDeclaration = "def selectEvolutions(pokepool, poke, oldEvos):";
        String funcBody = "\n\tresult = [] #example\n\tsameLetter = [pk for pk in pokepool if pk.name[0] == poke.name[0]]\n\tcount = 0\n\tfor other in sameLetter:\n\t\tevo = { \"evolveTo\": other, \"type\": EvolutionType.LEVEL, \"level\": RandomSource.nextInt(50) + 1 }\n\t\tresult.append(evo)\n\treturn result";

        if(peScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Pokemon");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Evolution");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "EvolutionType");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Items");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Moves");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Species");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addShopItemScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Selects items available in special shops",
                "#itemPool - an array of integers representing all available items",
                "#shop - a Shop object representing the shop the item will be available in",
                "#oldItem - an int representing the original shop item",
                "#",
                "#return: an int representing the new shop item",
                "#NOTE: you can access items through the imported Items class"
        };
        String funcDeclaration = "def selectShopItem(itemPool, shop, oldItem):";
        String funcBody = "\n\tif(\"Ball\" in toStr(oldItem, Index.ITEM)): #example\n\t\treturn Items.rareCandy\n\treturn oldItem";

        if(shScriptedRadioButton.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Items");
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.pokemon", "Shop");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public void addShopItemPriceScriptFunc()
    {
        String scriptText = sScriptInput.getText();
        String[] funcComments = {
                "#Selects items available in special shops",
                "#item - an int representing the shop item",
                "#balancedPrice - an int representing the \"balanced\" price of the item, this would have been selected by the \"balanced prices\" option",
                "#",
                "#return: an int representing the new price of the shop item",
                "#NOTE: you can access items through the imported Items class"
        };
        String funcDeclaration = "def selectShopItemPrice(item, balancedPrice):";
        String funcBody = "\n\tif(\"mail\" in toStr(item, Index.ITEM).lower()): #example\n\t\treturn balancedPrice * 10\n\tif(item == Items.rareCandy):\n\t\treturn 100\n\treturn balancedPrice";

        if(shScriptedPricesCheckbox.isSelected())
        {
            scriptText = addImport(scriptText, "com.dabomstew.pkrandom.constants", "Items");
            scriptText = addExampleFunc(scriptText, funcDeclaration, funcComments, funcBody);

            sScriptInput.setText(scriptText);
        }
    }

    public static void main(String[] args) {
        String firstCliArg = args.length > 0 ? args[0] : "";
        // invoke as CLI program
        if (firstCliArg.equals("cli")) {
            // snip the "cli" flag arg off the args array and invoke command
            String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
            int exitCode = CliRandomizer.invoke(commandArgs);
            System.exit(exitCode);
        } else {
            launcherInput = firstCliArg;
            if (launcherInput.equals("please-use-the-launcher")) usedLauncher = true;
            SwingUtilities.invokeLater(() -> {
                frame = new JFrame("NewRandomizerGUI");
                try {
                    String lafName = javax.swing.UIManager.getSystemLookAndFeelClassName();
                    // NEW: Only set Native LaF on windows.
                    if (lafName.equalsIgnoreCase("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
                        javax.swing.UIManager.setLookAndFeel(lafName);
                    }
                } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(NewRandomizerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                            ex);
                }
                frame.setContentPane(new NewRandomizerGUI().mainPanel);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            });
        }
    }
}
