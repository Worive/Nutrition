package ca.wescook.nutrition.events;

import ca.wescook.nutrition.configs.Config;
import ca.wescook.nutrition.effects.EffectsManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrition.NutritionProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventPlayerUpdate {
	private int decayCounter = 0;
	private int potionCounter = 0;

	@SubscribeEvent
	public void PlayerTickEvent(TickEvent.PlayerTickEvent event) {
		// Only run on server
		EntityPlayer player = event.player;
		if (player.getEntityWorld().isRemote)
			return;

		// Apply decay on configurable delay
		if (Config.enableDecay) {
			if (decayCounter > Config.decayRate) {
				nutritionDecay(player);
				decayCounter = 0;
			}
			decayCounter++;
		}

		// Re-check potion effects every 10 seconds
		if (potionCounter > 200) {
			EffectsManager.potionManage(player);
			potionCounter = 0;
		}
		potionCounter++;
	}

	private void nutritionDecay(EntityPlayer player) {
		if (player.getFoodStats().getFoodLevel() <= Config.decayHungerLevel) { // When the food level of the player is below the threshold
			for (Nutrient nutrient : NutrientList.get()) // Cycle through nutrient list
				player.getCapability(NutritionProvider.NUTRITION_CAPABILITY, null).subtract(nutrient, 0.1F); // And update player nutrition

		}
	}
}