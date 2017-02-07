/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.thoth.command.system;

import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.thoth.command.api.ThothCommandType;
import com.io7m.thoth.command.api.ThothResponse;
import javaslang.collection.List;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * A command that displays the available OSGi bundles.
 */

@Component(immediate = true, service = ThothCommandType.class)
public final class TCSystemBundles extends TCSystemCommand
{
  private BundleContext bundle_context;

  /**
   * Create a command.
   */

  public TCSystemBundles()
  {

  }

  private static String state(
    final Bundle bundle)
  {
    switch (bundle.getState()) {
      case Bundle.ACTIVE: {
        return "ACTIVE";
      }
      case Bundle.INSTALLED: {
        return "INSTALLED";
      }
      case Bundle.RESOLVED: {
        return "RESOLVED";
      }
      case Bundle.UNINSTALLED: {
        return "UNINSTALLED";
      }
    }

    throw new UnreachableCodeException();
  }

  /**
   * Activate the command with the given bundle context.
   *
   * @param context The bundle context
   */

  @Activate
  public void onActivate(
    final BundleContext context)
  {
    this.bundle_context = context;
  }

  @Override
  public String name()
  {
    return "bundles";
  }

  @Override
  public List<ThothResponse> execute(
    final List<String> text)
  {
    List<ThothResponse> results = List.empty();

    final Bundle[] bundles = this.bundle_context.getBundles();
    for (int index = 0; index < bundles.length; ++index) {
      final Bundle bundle = bundles[index];
      if (bundle.getSymbolicName() != null) {
        results = results.append(ThothResponse.of(String.format(
          "%-4d %-48s %-16s %-8s",
          Long.valueOf(bundle.getBundleId()),
          bundle.getSymbolicName(),
          bundle.getVersion(),
          state(bundle))));
      }
    }

    return results;
  }
}
