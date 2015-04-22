var risk = risk || {};

risk = risk || {};

risk.gameConfig = {
	mapColours: {
		territory: "rgba(255,255,255,0)",
		neighbour: "rgba(255,255,255,0.3)",
		select: "rgba(255,255,255,0.6)",
		hover: "rgba(255,255,255,0.4)"
	},
	customRules: {
		autoTerritory: "Auto-assign territories",
		autoPlace: "Auto-place starting battalions",
		fixedBonuses: "Incremental card bonuses"
	},
	mapSize: [900, 615],
	mute:0,
	muteLevel: ['Play all sounds', 'Play sound effects only', 'Mute all sounds'],
	server: 'ws://192.168.0.12:8080/bees-risk',
	defaultNames: ['Captain Seaman', 'General Surgery', 'Major Pain', 'Private Parts', 'Corporal Punishment']
}