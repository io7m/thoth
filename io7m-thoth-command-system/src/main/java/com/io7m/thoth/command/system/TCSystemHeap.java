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
import javaslang.collection.List;
import org.osgi.service.component.annotations.Component;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * A command that displays JVM heap statistics.
 */

@Component(immediate = true, service = ThothCommandType.class)
public final class TCSystemHeap extends TCSystemCommand
{
  /**
   * Construct a command.
   */

  public TCSystemHeap()
  {

  }

  @Override
  public String name()
  {
    return "heap";
  }

  @Override
  public List<String> execute(
    final List<String> text)
  {
    List<String> results = List.empty();

    for (final GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
      results = results.append(gcStats(bean));
    }

    results = results.append(TCSystemHeap.heapStats());
    return results;
  }

  private static String gcStats(
    final GarbageCollectorMXBean bean)
  {
    final double uptime =
      (double) ManagementFactory.getRuntimeMXBean().getUptime();
    final double gc_time =
      (double) bean.getCollectionTime();
    final double gc_percent =
      (gc_time / uptime) * 100.0;

    final StringBuilder sb = new StringBuilder(32);
    sb.append("gc ");
    sb.append(bean.getName());
    sb.append(" ");
    sb.append(bean.getCollectionCount());
    sb.append(" ");
    sb.append(gc_time);
    sb.append("ms ");
    sb.append(String.format("%.3f", Double.valueOf(gc_percent)));
    sb.append("%");
    return sb.toString();
  }

  private static String heapStats()
  {
    final MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    final MemoryUsage heap_usage = mbean.getHeapMemoryUsage();

    final double heap_used_bytes = (double) heap_usage.getUsed();
    final double heap_max_bytes = (double) heap_usage.getMax();
    final double heap_used_mega = heap_used_bytes / 1_000_000.0;
    final double heap_max_mega = heap_max_bytes / 1_000_000.0;

    return String.format(
      "heap %.2fmb / %.2fmb",
      Double.valueOf(heap_used_mega),
      Double.valueOf(heap_max_mega));
  }
}
