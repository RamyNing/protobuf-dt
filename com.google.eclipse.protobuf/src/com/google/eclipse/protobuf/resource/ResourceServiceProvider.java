/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import com.google.inject.Inject;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.IGlobalServiceProvider.ResourceServiceProviderImpl;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ResourceServiceProvider extends ResourceServiceProviderImpl {

  @Inject public ResourceServiceProvider(IResourceServiceProvider.Registry registry, IResourceServiceProvider provider) {
    super(registry, provider);
  }

  @Override public <T> T findService(EObject e, Class<T> serviceType) {
    if (e.eIsProxy()) {
      return findService(((InternalEObject) e).eProxyURI(), serviceType);
    }
    if (e.eResource() == null) return null;
    return findService(e.eResource().getURI(), serviceType);
  }
}