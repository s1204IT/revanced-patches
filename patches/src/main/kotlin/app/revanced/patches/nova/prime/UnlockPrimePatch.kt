package app.revanced.patches.nova.prime

import app.revanced.patcher.Fingerprint
import app.revanced.patcher.extensions.InstructionExtensions
import app.revanced.patcher.fingerprint
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11x

private val primeFingerprint: Fingerprint = fingerprint {
    opcodes(
        Opcode.IPUT_OBJECT,
        Opcode.CONST_STRING,
        Opcode.CONST_4,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT,
    )
    strings("1")
}

val unlockPrimePatch = bytecodePatch(
    name = "unlock-prime",
    description = "Unlocks Nova Prime and all functions of the app.",
) {
    compatibleWith("com.teslacoilsw.launcher")

    execute {
        val method = primeFingerprint.method ?: return@execute

        val insertIndex = primeFingerprint.patternMatch
            ?.endIndex
            ?.plus(1)
            ?: return@execute

        val instructions = method.implementation?.instructions
            ?: return@execute

        if (insertIndex !in 1..instructions.size) return@execute

        val primeStatusRegister =
            (instructions[insertIndex - 1] as? BuilderInstruction11x)?.registerA
                ?: return@execute

        with(InstructionExtensions) {
            method.addInstruction(
                insertIndex,
                "const/16 v$primeStatusRegister, 0x200",
            )
        }
    }
}
