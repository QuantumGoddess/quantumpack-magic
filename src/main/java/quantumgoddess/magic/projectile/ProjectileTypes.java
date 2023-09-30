package quantumgoddess.magic.projectile;


import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

public enum ProjectileTypes implements ProjectileType{
    FIRE(() -> Ingredient.ofItems(Items.FIRE_CHARGE));

    private final Supplier<Ingredient> repairIngredient;

    private ProjectileTypes(Supplier<Ingredient> repairIngredient) {
        this.repairIngredient = Suppliers.memoize(repairIngredient);
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
    
}
