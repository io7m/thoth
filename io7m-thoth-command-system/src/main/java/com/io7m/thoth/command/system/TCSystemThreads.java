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

import com.io7m.thoth.command.api.ThothCommandType;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.SortedMap;
import org.osgi.service.component.annotations.Component;

import java.util.Set;
import java.util.function.Function;

/**
 * List the current JVM threads.
 */

@Component(immediate = true, service = ThothCommandType.class)
public final class TCSystemThreads extends TCSystemCommand
{
  /**
   * Construct a command.
   */

  public TCSystemThreads()
  {

  }

  @Override
  public String name()
  {
    return "threads";
  }

  @Override
  public List<String> execute(
    final List<String> text)
  {
    List<String> results = List.empty();

    final Set<Thread> threads = Thread.getAllStackTraces().keySet();
    final SortedMap<Long, Thread> by_id =
      HashSet.ofAll(threads)
        .toMap(th -> Long.valueOf(th.getId()), Function.identity())
        .toSortedMap(Function.identity());

    for (final Long id : by_id.keySet()) {
      final Thread thread = by_id.get(id).get();
      results = results.append(String.format(
        "%-4d %-32s %-8s", id, thread.getName().trim(), thread.getState()));
    }
    return results;
  }
}
