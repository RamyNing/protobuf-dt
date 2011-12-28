/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages;

import static com.google.eclipse.protobuf.ui.preferences.pages.Messages.*;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToButtonSelection.bindSelectionOf;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceBinder;
import com.google.inject.Inject;

/**
 * Base class for pages that set up both "Workspace Preferences" and "Project Properties."
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public abstract class PreferenceAndPropertyPage extends PreferencePage implements IWorkbenchPreferencePage,
    IWorkbenchPropertyPage {

  private Button btnEnableProjectSettings;

  private Link lnkEnableWorkspaceSettings;

  private IProject project;
  private Map<String, Object> dataMap;

  @Inject private IPreferenceStoreAccess preferenceStoreAccess;

  private final PreferenceBinder preferenceBinder = new PreferenceBinder();

  @Override protected Control createContents(Composite parent) {
    Composite contents = contentParent(parent);
    doCreateContents(contents);
    if (isPropertyPage()) {
      setupBindingOfBtnEnabledProjectSettings();
    }
    setupBinding(preferenceBinder);
    preferenceBinder.applyValues();
    updateContents();
    return contents;
  }

  /**
   * Creates the <code>{@link Composite}</code> that will contain all the UI controls in this preference page. By
   * default it returns a <code>{@link Composite}</code> that contains the options to switch between "Project" and
   * "Workspace" settings.
   * @param parent the parent {@code Composite}.
   * @return the created {@code Composite}.
   */
  protected Composite contentParent(Composite parent) {
    return switchBetweenProjectAndWorkspaceSettings(parent);
  }

  private Composite switchBetweenProjectAndWorkspaceSettings(Composite parent) {
    // generated by WindowBuilder
    Composite contents = new Composite(parent, NONE);
    contents.setLayout(new GridLayout(3, false));
    if (isPropertyPage()) {
      btnEnableProjectSettings = new Button(contents, SWT.CHECK);
      btnEnableProjectSettings.setText(enableProjectSettings);

      lnkEnableWorkspaceSettings = new Link(contents, SWT.NONE);
      lnkEnableWorkspaceSettings.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      lnkEnableWorkspaceSettings.setText("<a>" + configureWorkspaceSettings + "</a>");

      Label label = new Label(contents, SWT.SEPARATOR | SWT.HORIZONTAL);
      label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    }
    new Label(contents, SWT.NONE);
    addEventListeners();
    return contents;
  }

  private void addEventListeners() {
    if (isPropertyPage()) {
      btnEnableProjectSettings.addSelectionListener(new SelectionAdapter() {
        @Override public void widgetSelected(SelectionEvent e) {
          onProjectSettingsActivation(areProjectSettingsActive());
          updateEnableWorkspaceSettingsLink();
        }
      });
      lnkEnableWorkspaceSettings.addSelectionListener(new SelectionAdapter() {
        @Override public void widgetSelected(SelectionEvent e) {
          openWorkspacePreferences();
        }
      });
    }
  }

  /**
   * Notification that the "Enable project specific settings" check button has been selected.
   * @param projectSettingsActive indicates the selection of the "Enable project specific settings" check button.
   */
  protected abstract void onProjectSettingsActivation(boolean projectSettingsActive);

  private void openWorkspacePreferences() {
    String preferencePageId = preferencePageId();
    createPreferenceDialogOn(getShell(), preferencePageId , new String[] { preferencePageId }, dataMap).open();
  }

  /**
   * Creates the contents of this preference page.
   * @param parent the parent {@code Composite}.
   */
  protected abstract void doCreateContents(Composite parent);

  private void setupBindingOfBtnEnabledProjectSettings() {
    String preferenceName = enableProjectSettingsPreferenceName();
    if (isEmpty(preferenceName)) {
      return;
    }
    BooleanPreference preference = new BooleanPreference(preferenceName, getPreferenceStore());
    preferenceBinder.add(bindSelectionOf(btnEnableProjectSettings).to(preference));
  }

  /**
   * Returns the name of the preference that specifies whether this page is a "Project Properties" or a "Workspace
   * Preferences" page.
   * @return the name of the preference that specifies whether this page is a "Project Properties" or a "Workspace
   * Preferences" page.
   */
  protected abstract String enableProjectSettingsPreferenceName();

  /**
   * Sets up data binding.
   * @param preferenceBinder the preference binder;
   */
  protected abstract void setupBinding(PreferenceBinder preferenceBinder);

  /**
   * Returns the id of this preference page.
   * @return the id of this preference page.
   */
  protected abstract String preferencePageId();

  /**
   * Returns the <code>{@link IProject}</code> that owns the properties shown in this page.
   * @return the project that owns the properties shown in this page.
   */
  @Override public final IAdaptable getElement() {
    return project;
  }

  /**
   * Sets the <code>{@link IProject}</code> that owns the properties shown in this page.
   * @param element the {@code IAdaptable} associated with the project that owns the properties shown in this page.
   */
  @Override public final void setElement(IAdaptable element) {
    this.project = (IProject) element.getAdapter(IProject.class);
  }

  /**
   * Returns the preference store of this preference page.
   * @return the preference store.
   */
  @Override protected final IPreferenceStore doGetPreferenceStore() {
    if (isPropertyPage()) {
      return preferenceStoreAccess.getWritablePreferenceStore(currentProject());
    }
    return preferenceStoreAccess.getWritablePreferenceStore();
  }

  /**
   * Indicates whether this page is a "Project Properties" or a "Workspace Preferences" page.
   * @return {@code true} if this page is a "Project Properties" page, or {@code false} if this page is a
   * "Workspace Preferences" page.
   */
  protected final boolean isPropertyPage() {
    return project != null;
  }

  private IProject currentProject() {
    if (project == null) {
      throw new IllegalStateException("Not a property page case, but current project was requested.");
    }
    return project;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override public final void applyData(Object data) {
    if (data instanceof Map) {
      this.dataMap = (Map<String, Object>) data;
    }
  }

  /**
   * Activates or deactivates the project-specific settings.
   * @param active indicates whether the project-specific settings should be active or not.
   */
  protected final void activateProjectSettings(boolean active) {
    btnEnableProjectSettings.setSelection(active);
    updateEnableWorkspaceSettingsLink();
  }

  private void updateEnableWorkspaceSettingsLink() {
    lnkEnableWorkspaceSettings.setEnabled(!areProjectSettingsActive());
  }

  /**
   * Indicates if the project-specific settings are active or not.
   * @return {@code true} if the project-specific settings are active; {@code false} otherwise.
   */
  protected final boolean areProjectSettingsActive() {
    return btnEnableProjectSettings.getSelection();
  }

  /** {@inheritDoc} */
  @Override public void init(IWorkbench workbench) {}

  @Override public final boolean performOk() {
    preferenceBinder.saveValues();
    okPerformed();
    return true;
  }

  /** Method invoked after this page's values have been saved. By default this method does nothing. */
  protected void okPerformed() {}

  @Override protected final void performDefaults() {
    preferenceBinder.applyDefaults();
    updateContents();
    super.performDefaults();
  }

  /** Refreshes this page with the stored/default preference values. */
  protected void updateContents() {}

  /** Marks this page as "valid." */
  protected final void pageIsNowValid() {
    setErrorMessage(null);
    setValid(true);
  }

  /**
   * Marks this page as "invalid."
   * @param errorMessage the error message to display.
   */
  protected final void pageIsNowInvalid(String errorMessage) {
    setErrorMessage(errorMessage);
    setValid(false);
  }

  /**
   * Returns the project whose properties are being changed.
   * @return the project whose properties are being changed, or {@code null} if this page is updating workspace-level
   * preferences.
   */
  protected final IProject project() {
    return project;
  }
}

