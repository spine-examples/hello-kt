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
        with(builder()) {
            username = command.username
            addLines(command.text)
        }

//        update {
//            it.username = command.username
//        }

        println("[$user] $output")
        return with(Printed.newBuilder()) {
            username = user
            text = output
            vBuild()
        }
    }
}

/*
private fun <E : TransactionalEntity<*, S, B>, S : EntityState, B : ValidatingBuilder<S>>
        builderOf(entity: E) {
}

inline fun <E : TransactionalEntity<*, S, B>, S : EntityState, B : ValidatingBuilder<S>>
        E.update(block: (builder: B) -> Unit) {
    with(this.builder(), block)
}
*/
