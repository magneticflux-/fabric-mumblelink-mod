package com.skaggsm.mumblelinkmod.client

import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

/**
 * Convert to a float 3-array in a left-handed coordinate system.
 * Minecraft is right-handed by default, Mumble needs left-handed.
 *
 * @see <a href="https://wiki.mumble.info/wiki/Link#Coordinate_system">Coordinate system</a>
 */
val Vec3d.toLHArray: FloatArray
    get() = floatArrayOf(x.toFloat(), y.toFloat(), -z.toFloat())

/**
 * A stable hash function designed for world IDs.
 * Different clients should be able to run this on the same world ID and get the same result.
 *
 * Based on the `djb2` hash function: [Hash Functions](http://www.cse.yorku.ca/~oz/hash.html)
 */
val Identifier.stableHash: Int
    get() {
        var hash = 5381

        for (c in this.toString()) {
            hash += (hash shl 5) + c.code
        }

        return hash
    }
