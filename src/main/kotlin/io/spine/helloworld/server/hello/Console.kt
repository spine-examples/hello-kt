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

package io.spine.helloworld.server.hello

import io.spine.helloworld.hello.command.Print
import io.spine.helloworld.hello.event.Printed
import io.spine.server.command.Assign
import io.spine.server.entity.TransactionalEntity
import io.spine.server.procman.ProcessManager

/**
 * This Process Manager handles the [Print] command.
 */
internal class Console : ProcessManager<String, Output, Output.Builder>() {

    /** Handles the print command. */
    @Assign
    fun handle(command: Print): Printed {
        val user = command.username
        val output = command.text

        update {
            username = user
            addLines(output)
        }

        println("[$user] $output")
        return with(Printed.newBuilder()) {
            username = user
            text = output
            vBuild()
        }
    }

    /**
     * Allows to call `update` instead of `with(builder()` in handler methods.
     *
     * @apiNote TODO:2020-12-16:alexander.yevsyukov: Try to implement the following extension for
     * [TransactionalEntity] similarly to how this inline function works.
     * It is not possible now because even inline functions are now allowed to access protected
     * methods and `this.builder()` is not accessible.
     * ```kotlin
     * inline fun <E : TransactionalEntity<*, S, B>, S : EntityState, B : ValidatingBuilder<S>>
     *         E.update(block: B.() -> Unit): B {
     *     val builder = this.builder()
     *     block.invoke(builder)
     *     return builder
     * }
     * ```
     * We can brute-force it via Reflection and have a private extension function `E.builderOf()`
     * somewhere next to `E.update()` above, but it would mean some performance penalty.
     */
    private inline fun update(block: Output.Builder.() -> Unit): Output.Builder {
        val builder = this.builder()
        block.invoke(builder)
        return builder
    }
}
