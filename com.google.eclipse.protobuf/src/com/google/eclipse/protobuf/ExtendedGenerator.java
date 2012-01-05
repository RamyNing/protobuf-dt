/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf;

import com.google.inject.*;

import org.eclipse.xtext.*;
import org.eclipse.xtext.generator.Generator;
import org.eclipse.xtext.xtext.ecoreInference.IXtext2EcorePostProcessor;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ExtendedGenerator extends Generator {
  public ExtendedGenerator() {
    new XtextStandaloneSetup() {
      @Override public Injector createInjector() {
        return Guice.createInjector(new XtextRuntimeModule() {
            @Override public Class<? extends IXtext2EcorePostProcessor> bindIXtext2EcorePostProcessor() {
              return ProtobufEcorePostProcessor.class;
          }
        });
      }
    }.createInjectorAndDoEMFRegistration();
  }
}
