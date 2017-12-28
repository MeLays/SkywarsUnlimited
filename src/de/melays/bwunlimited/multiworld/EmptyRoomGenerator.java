/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.multiworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class EmptyRoomGenerator extends ChunkGenerator{
	
	boolean bedrock;
	
	public EmptyRoomGenerator (boolean bedrock) {
		this.bedrock = bedrock;
	}
	
	@SuppressWarnings("deprecation")
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid){
		byte[][] result = new byte[world.getMaxHeight() / 16][];
		
		if (bedrock) {
			for(int x = 0; x < 16; x++){
				for(int z = 0; z < 16; z++){
				setBlock(result, x, 0, z, (byte)Material.BEDROCK.getId());
				}
			}
		}
		
		return result;
	}
	
	void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
	    if (result[y >> 4] == null) {
	        result[y >> 4] = new byte[4096];
	    }
	    result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
	}
}
