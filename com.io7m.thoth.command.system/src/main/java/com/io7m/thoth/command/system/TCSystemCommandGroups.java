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

import java.util.Objects;
import com.io7m.thoth.command.api.ThothCommandType;
import com.io7m.thoth.command.api.ThothResponse;
import io.vavr.collection.List;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

/**
 * List the available command groups.
 */

@Component(immediate = true, service = ThothCommandType.class)
public final class TCSystemCommandGroups extends TCSystemCommand
{
  private BundleContext context;

  /**
   * Construct a command.
   */

  public TCSystemCommandGroups()
  {

  }

  /**
   * Activate the component.
   *
   * @param in_context The bundle context
   */

  @Activate
  public void onActivate(
    final BundleContext in_context)
  {
    this.context = Objects.requireNonNull(in_context, "Context");
  }

  @Override
  public String name()
  {
    return "command-groups";
  }

  @Override
  public List<ThothResponse> execute(
    final List<String> text)
  {
    final ServiceTracker<ThothCommandType, ThothCommandType> tracker =
      new ServiceTracker<>(this.context, ThothCommandType.class, null);

    tracker.open();

    try {
      final ServiceReference<ThothCommandType>[] available =
        tracker.getServiceReferences();

      SortedSet<String> groups = TreeSet.empty();
      for (int index = 0; index < available.length; ++index) {
        final ServiceReference<ThothCommandType> ref = available[index];
        final ThothCommandType command = this.context.getService(ref);
        if (command != null) {
          groups = groups.add(command.group());
        }
      }

      return groups.toList().map(ThothResponse::of);
    } finally {
      tracker.close();
    }
  }
}
