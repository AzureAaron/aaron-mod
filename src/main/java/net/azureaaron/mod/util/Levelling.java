package net.azureaaron.mod.util;

public class Levelling {

	public static int getDungeonLevel(long xp) {
		int[] xpChart = { 50, 75, 110, 160, 230, 330, 470, 670, 950, 1340, 1890, 2665, 3760, 5260, 7380, 10300, 14400,
				20000, 27600, 38000, 52500, 71500, 97000, 132000, 180000, 243000, 328000, 445000, 600000, 800000,
				1065000, 1410000, 1900000, 2500000, 3300000, 4300000, 5600000, 7200000, 9200000, 12000000, 15000000,
				19000000, 24000000, 30000000, 38000000, 48000000, 60000000, 75000000, 93000000, 116250000 };

		long xpTotal = 0;
		long xpLeft = xp;
		int level = 1;

		for (int i = 0; i < xpChart.length; i++) {
			xpTotal += xpChart[i];
			xpLeft -= xpChart[i];
			level = i + 1;

			if (xp < xpTotal) {
				level = i;
				break;
			}
		}
		if(level == 50) {
			while (xpLeft >= 200000000) {
				level++;
				xpLeft -= 200000000;
			}
		}
		return level;
	}

	public static int getSkillLevel(long xp, String skill, int capIncrease) {
		int[] regularXpChart = { 50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500, 5000, 7500, 10000, 15000, 20000,
				30000, 50000, 75000, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000,
				1100000, 1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000,
				2200000, 2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000, 4000000,
				4300000, 4600000, 4900000, 5200000, 5500000, 5800000, 6100000, 6400000, 6700000, 7000000 };

		int[] runecraftingXpChart = { 50, 100, 125, 160, 200, 250, 315, 400, 500, 625, 785, 1000, 1250, 1600, 2000,
				2465, 3125, 4000, 5000, 6200, 7800, 9800, 12200, 15300, 19050 };

		int[] socialXpChart = { 50, 100, 150, 250, 500, 750, 1000, 1250, 1500, 2000, 2500, 3000, 3750, 4500, 6000, 8000,
				10000, 12500, 15000, 20000, 25000, 30000, 35000, 40000, 50000 };

		enum skillCaps {
			ALCHEMY(50), CARPENTRY(50), COMBAT(60), ENCHANTING(60), FARMING(50), FISHING(50), FORAGING(50), MINING(60),
			RUNECRAFTING(25), SOCIAL(25), TAMING(50);
			public int cap;
			private skillCaps(int cap) {
				this.cap = cap;
			}
		};

		if (capIncrease != 0)
			skillCaps.valueOf(skill).cap += capIncrease;

		int[] xpChart = regularXpChart;
		if (skill.equals("RUNECRAFTING"))
			xpChart = runecraftingXpChart;
		if (skill.equals("SOCIAL"))
			xpChart = socialXpChart;

		long xpTotal = 0;
		int level = 1;

		for (int i = 0; i < xpChart.length; i++) {
			xpTotal += xpChart[i];
			level = i + 1;

			if (xp < xpTotal) {
				level = i;
				break;
			}
		}
		return Math.min(level, skillCaps.valueOf(skill).cap);

	};
	
	public static int getSkyblockLevel(int xp) {
		int xpLeft = xp;
		int level = 0;
				
		while(xpLeft >= 100) {
			level++;
			xpLeft -= 100;
		}
		
		return level;
	}
	
	public static int getSlayerLevel(int xp, String slayer) {
		int[] revenantXpChart = { 5, 15, 200, 1000, 5000, 20000, 100000, 400000, 1000000 };
		int[] tarantulaXpChart = { 5, 25, 200, 1000, 5000, 20000, 100000, 400000, 1000000 };
		int[] svenXpChart = { 10, 30, 250, 1500, 5000, 20000, 100000, 400000, 1000000 };
		int[] voidgloomXpChart = { 10, 30, 250, 1500, 5000, 20000, 100000, 400000, 1000000 };
		int[] infernoXpChart = { 10, 30, 250, 1500, 5000, 20000, 100000, 400000, 1000000 };
		int[] riftstalkerXpChart = { 20, 75, 240, 840, 2400 };
		int[] xpChart = {};
		
		switch(slayer) {
		case "REVENANT_HORROR":
			xpChart = revenantXpChart;
			break;
		case "TARANTULA_BROODFATHER":
			xpChart = tarantulaXpChart;
			break;
		case "SVEN_PACKMASTER":
			xpChart = svenXpChart;
			break;
		case "VOIDGLOOM_SERAPH":
			xpChart = voidgloomXpChart;
			break;
		case "INFERNO_DEMONLORD":
			xpChart = infernoXpChart;
			break;
		case "RIFTSTALKER_BLOODFIEND":
			xpChart = riftstalkerXpChart;
			break;
		}
		
		if(xpChart.length > 5 && xp >= xpChart[8]) return 9;
		if(xpChart.length > 5 && xp >= xpChart[7]) return 8;
		if(xpChart.length > 5 && xp >= xpChart[6]) return 7;
		if(xpChart.length > 5 && xp >= xpChart[5]) return 6;
		if(xp >= xpChart[4]) return 5;
		if(xp >= xpChart[3]) return 4;
		if(xp >= xpChart[2]) return 3;
		if(xp >= xpChart[1]) return 2;
		if(xp >= xpChart[0]) return 1;
		return 0;
	}
}
