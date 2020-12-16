/*
 * Copyright 2020, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.spine.helloworld.server

import io.spine.server.ServerEnvironment
import io.spine.base.Production
import io.spine.helloworld.server.hello.HelloContext
import io.spine.server.storage.memory.InMemoryStorageFactory
import io.spine.server.delivery.Delivery
import io.spine.server.transport.memory.InMemoryTransportFactory
import kotlin.Throws
import java.io.IOException
import io.spine.server.Server

/**
 * Backend implementation of the Hello Context.
 */
internal class Server(serverName: String) {

    /**
     * Configures the production server-side environment.
     *
     * We use in-memory implementations (that are typically used in tests) to simplify this
     * example application. Real applications would use implementations that correspond
     * to their environments.
     */
    companion object Environment {
        init {
            with(ServerEnvironment.`when`(Production::class.java)) {
                use(InMemoryStorageFactory.newInstance())
                use(Delivery.localAsync())
                use(InMemoryTransportFactory.newInstance())
            }
        }
    }

    private val server: Server = Server.inProcess(serverName)
        .add(HelloContext.builder())
        .build()

    /** Starts the server. */
    @Throws(IOException::class)
    fun start() = server.start()

    /** Shuts downs the server. */
    fun shutdown() = server.shutdown()
}
