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
	mute:1,
	server: 'ws://localhost:8080'
}