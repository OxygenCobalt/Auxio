package org.oxycblt.auxio.music.stack.interpret.prepare


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oxycblt.auxio.music.stack.interpret.Interpreter
import org.oxycblt.auxio.music.stack.interpret.InterpreterImpl

@Module
@InstallIn(SingletonComponent::class)
interface PrepareModule {
    @Binds fun prepare(factory: PreparerImpl): Preparer
}
