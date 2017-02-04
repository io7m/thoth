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

import com.io7m.jnull.NullCheck;
import com.io7m.thoth.command.api.ThothCommandType;
import com.io7m.thoth.command.api.ThothResolverType;
import com.io7m.thoth.command.api.ThothResponse;
import javaslang.collection.List;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * A command that displays the available listener groups.
 */

@Component(immediate = true, service = ThothCommandType.class)
public final class TCSystemListenerGroups extends TCSystemCommand
{
  private ThothResolverType resolver;

  /**
   * Construct a command.
   */

  public TCSystemListenerGroups()
  {

  }

  /**
   * Register a resolver.
   *
   * @param in_resolver The resolver
   */

  @Reference
  public void onRegisterResolver(
    final ThothResolverType in_resolver)
  {
    this.resolver = NullCheck.notNull(in_resolver, "Resolver");
  }

  @Override
  public String name()
  {
    return "listener-groups";
  }

  @Override
  public List<ThothResponse> execute(
    final List<String> text)
  {
    return this.resolver.commandGroups().toList().map(ThothResponse::of);
  }
}
