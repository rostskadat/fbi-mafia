package com.stratio.fbi.mafia.model.org;

import java.util.Iterator;

import com.stratio.fbi.mafia.model.Mafioso;

public interface MafiaOrganization {

	void setDeep(Boolean isDeep);

	Boolean isDeep();

	void setCupula(Mafioso mafioso);

	Mafioso getCupula();

	/**
	 * THis method returns the boss of a given {@link Mafioso}. Basically allow
	 * to navigate up the tree.
	 * 
	 * @param mafioso
	 *            the {@link Mafioso} of which to return the boss.
	 * @return the {@link Mafioso} whose the boss or {@code null} if it's the
	 *         top dog
	 */
	Mafioso getBoss(Mafioso mafioso);

	// XXX: Should it be part of the iterator.remove()
	void remove(Mafioso mafioso);

	void addSubordinate(Mafioso boss, Mafioso subordinate);

	Iterator<Mafioso> getSubordinates(Mafioso boss);

	Iterator<Mafioso> iterator();

}
