/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.execution.actions;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Describes a run configuration being created by a context run action.
 *
 * @author yole
 */
public abstract class ConfigurationFromContext {
  @NotNull
  public abstract RunnerAndConfigurationSettings getConfigurationSettings();

  public abstract void setConfigurationSettings(RunnerAndConfigurationSettings configurationSettings);

  @NotNull
  public RunConfiguration getConfiguration() {
    return getConfigurationSettings().getConfiguration();
  }

  @NotNull
  public ConfigurationType getConfigurationType() {
    return getConfiguration().getType();
  }

  @NotNull
  public abstract PsiElement getSourceElement();

  /**
   * Called before the configuration created from context is first executed. Can be used to show additional UI for customizing the
   * created configuration.
   *
   * @param context       the context
   * @param startRunnable the runnable that needs to be called after additional customization is complete.
   */
  public void onFirstRun(ConfigurationContext context, Runnable startRunnable) {
    startRunnable.run();
  }

  public boolean isPreferredTo(ConfigurationFromContext other) {
    return true;
  }

  public boolean isProducedBy(Class<? extends RunConfigurationProducer> producerClass) {
    return false;
  }

  public static final Comparator<ConfigurationFromContext> COMPARATOR = new Comparator<ConfigurationFromContext>() {
    @Override
    public int compare(ConfigurationFromContext configuration1, ConfigurationFromContext configuration2) {
      if (PsiTreeUtil.isAncestor(configuration1.getSourceElement(), configuration2.getSourceElement(), true)) {
        return 1;
      }
      if (PsiTreeUtil.isAncestor(configuration2.getSourceElement(), configuration1.getSourceElement(), true)) {
        return -1;
      }
      if (!configuration1.isPreferredTo(configuration2)) {
        return 1;
      }
      if (!configuration2.isPreferredTo(configuration1)) {
        return -1;
      }
      return 0;
    }
  };

  public static final Comparator<ConfigurationFromContext> NAME_COMPARATOR = new Comparator<ConfigurationFromContext>() {
    @Override
    public int compare(final ConfigurationFromContext p1, final ConfigurationFromContext p2) {
      return p1.getConfigurationType().getDisplayName().compareTo(p2.getConfigurationType().getDisplayName());
    }
  };
}
