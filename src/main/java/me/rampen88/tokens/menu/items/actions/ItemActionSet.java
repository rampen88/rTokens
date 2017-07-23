package me.rampen88.tokens.menu.items.actions;

import me.rampen88.tokens.Tokens;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ItemActionSet implements ItemAction{

	private Set<ItemAction> actions = new HashSet<>();

	public void addAction(ItemAction action){
		actions.add(action);
	}

	@Override
	public void executeAction(Player p, Tokens plugin) {
		if(actions.size() > 0)
			actions.forEach(a -> a.executeAction(p, plugin));
	}


}
