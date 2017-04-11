/**
 * Copyright 2015 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.lib.io;

import com.streamsets.pipeline.api.ext.io.OverrunException;
import com.streamsets.pipeline.api.impl.Utils;
import com.streamsets.pipeline.lib.util.ExceptionUtils;

import java.io.InputStream;

public class OverrunInputStream extends CountingInputStream {

  private final int readLimit;
  private boolean enabled;

  public OverrunInputStream(InputStream in, int readLimit, boolean enabled) {
    super(in);
    this.readLimit = readLimit;
    this.enabled = enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled && readLimit > 0;
    if (enabled) {
      resetCount();
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  protected synchronized void afterRead(int n) {
    super.afterRead(n);
    if (isEnabled() && getCount() > readLimit) {
      ExceptionUtils.throwUndeclared(new OverrunException(Utils.format(
        "Reader exceeded the read limit '{}'", readLimit), getPos()));
    }
  }
}
