package com.skaggsm.mumblelinkmod.client

import kotlinx.serialization.Serializable
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld

@Serializable
data class Identity(val name: String, val worldSpawn: IntArray, val dimension: String) {
    constructor(world: ClientWorld, player: ClientPlayerEntity) : this(
        player.displayName.asString(),
        intArrayOf(
            world.spawnPos.x,
            world.spawnPos.y,
            world.spawnPos.z
        ),
        player.entityWorld.registryKey.value.toString()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Identity

        if (name != other.name) return false
        if (!worldSpawn.contentEquals(other.worldSpawn)) return false
        if (dimension != other.dimension) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + worldSpawn.contentHashCode()
        result = 31 * result + dimension.hashCode()
        return result
    }
}
