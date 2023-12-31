package edu.school21.viewmodels.handlers;

import edu.school21.enums.ScreenType;
import edu.school21.viewmodels.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class WindowManager {

    private final AnchorPane anchorPane;
    private Stage basicStage;
    private Stage aboutStage;
    private Stage creditStage;
    private Stage depositStage;
    private Stage settingsStage;

    private CreditViewModel creditViewModel;
    private DepositViewModel depositViewModel;
    private SettingsViewModel settingsViewModel;

    private final Settings settings;

    public WindowManager(
            final AnchorPane anchorPane,
            final Stage basicStage
    ) {
        this.anchorPane = anchorPane;
        this.basicStage = basicStage;
        settings = new Settings();
    }

    /**
     * Открывает одно из основных или модальных окон в зависимости от переданного типа
     */
    public void openWindow(final ScreenType newType) {
        if (newType == ScreenType.ABOUT) {
            openAboutWindow();
        } else if (newType == ScreenType.SETTINGS) {
            openSettingsWindow();
        } else {
            openCommonWindow(newType);
        }
    }

    private void openAboutWindow() {
        if (aboutStage == null) {
            aboutStage = createStage(ScreenType.ABOUT);
        }
        aboutStage.show();
    }

    private void openSettingsWindow() {
        if (settingsStage == null) {
            settingsStage = createStage(ScreenType.SETTINGS);
            settingsViewModel.setWindowManager(this);
        }
        settingsViewModel.setSettingFromProperties(settings);
        settingsStage.show();
    }

    private void openCommonWindow(final ScreenType newType) {
        Stage newStage = getStageByScreenType(newType);
        if (newStage == null) {
            newStage = createStage(newType);
        }
        newStage.show();
    }

    public static void showErrorMessage(final String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Закрывает все приложение, если было закрыто одно из основных окон
     */
    public void closeWindow() {
        final Stage stage = getStageByScreenType(ScreenType.BASIC);
        if (stage != null) {
            stage.close();
        }
    }

    public void closeSettingsViewAndUpdate() {
        settingsStage.close();
        settings.setStyleSheetFromSettings();
        updateStyle();
    }

    public void saveSettings() {
        settings.savePropertiesToFile();
    }

    public void updateSettings(
            final String backgroundColor,
            final String buttonBackgroundColor,
            final String buttonTextColor,
            final String buttonBorderColor,
            final String buttonFontSize
    ) {
        settings.setStyleToProperties(
                backgroundColor,
                buttonBackgroundColor,
                buttonTextColor,
                buttonBorderColor,
                buttonFontSize
        );
    }

    /**
     * Обновление стилей всех окон
     */
    public void updateStyle() {
        settings.setStyleSheetFromSettings();

        anchorPane.getStylesheets().clear();
        anchorPane.getStylesheets().add(Settings.STYLESHEET);

        updateChildStyleIfNotNull(settingsViewModel);
        updateChildStyleIfNotNull(creditViewModel);
        updateChildStyleIfNotNull(depositViewModel);
    }

    private void updateChildStyleIfNotNull(final ChildViewModel viewModel) {
        if (viewModel != null) {
            viewModel.updateStyle();
        }
    }

    private Stage createStage(final ScreenType type) {
        final URL url = getClass().getResource(type.getFileName());
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        Parent root = null;

        try {
            root = loader.load();
        } catch (final IOException e) {
            showErrorMessage(e.getMessage());
        }

        final ChildViewModel viewModel = loader.getController();
        if (viewModel != null) {
            viewModel.initialize();
            setViewModelByScreenType(viewModel, type);
        }

        final Stage stage = new Stage();
        stage.setResizable(false);
        stage.initOwner(basicStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(type.getTitle());
        stage.setScene(new Scene(root));
        setStageByScreenType(type, stage);
        return stage;
    }

    private void setStageByScreenType(final ScreenType type, final Stage stage) {
        if (type == ScreenType.CREDIT) {
            creditStage = stage;
        } else if (type == ScreenType.DEPOSIT) {
            depositStage = stage;
        } else if (type == ScreenType.BASIC) {
            basicStage = stage;
        }
    }

    private Stage getStageByScreenType(final ScreenType type) {
        if (type == ScreenType.CREDIT) {
            return creditStage;
        } else if (type == ScreenType.DEPOSIT) {
            return depositStage;
        } else if (type == ScreenType.BASIC) {
            return basicStage;
        }
        return null;
    }

    private void setViewModelByScreenType(final ChildViewModel viewModel, final ScreenType type) {
        if (type == ScreenType.CREDIT) {
            creditViewModel = (CreditViewModel) viewModel;
        } else if (type == ScreenType.DEPOSIT) {
            depositViewModel = (DepositViewModel) viewModel;
        } else if (type == ScreenType.SETTINGS) {
            settingsViewModel = (SettingsViewModel) viewModel;
        }
    }
}
