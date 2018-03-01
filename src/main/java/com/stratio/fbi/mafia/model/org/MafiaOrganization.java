package com.stratio.fbi.mafia.model.org;

import java.util.Iterator;

import com.stratio.fbi.mafia.model.Mafioso;

public interface MafiaOrganization {

    void setDeepCount(Boolean isDeepCount);

	Boolean isDeepCount();

    /**
     * This method completely erase the organization.
     */
    void erase();

	void setCupula(Mafioso mafioso);

	Mafioso getCupula();

	/**
     * This method returns the boss of a given {@link Mafioso}. Basically allow to navigate up the tree.
     * 
     * @param mafioso
     *            the {@link Mafioso} of which to return the boss.
     * @return the {@link Mafioso} whose the boss or {@code null} if it's the top dog
     */
	Mafioso getBoss(Mafioso mafioso);

    /**
     * This method remove the given {@link Mafioso} from the underlying organization implementation It will implement
     * the famous Mafia age based promotion algorithm (the oldest {@link Mafioso} subordinate take charge)
     * 
     * @param mafioso
     *            the {@link Mafioso} to remove
     */
    void removeFromOrganization(Mafioso mafioso);

    /**
     * This method will add a {@code subordinate} to the given {@code boss}. It is not to be confused with the
     * 
     * @param boss
     *            the {@link Mafioso} to add the {@code subordinate} to
     * @param subordinate
     *            the {@link Mafioso} to add the {@code boss}'s list of subordinate
     */
	void addSubordinate(Mafioso boss, Mafioso subordinate);

    /**
     * This method capture a {@link Mafioso} position within an organization. It is used to re-establish his position
     * once freed from Jail.
     * 
     * @param mafioso
     * @return
     */
    MafiosoPosition getMafiosoPosition(Mafioso mafioso);

    /**
     * This method is used to reinstate a specific {@link Mafioso} to its previous position in the organization
     * 
     * @param position
     */
    void reinstateMafioso(MafiosoPosition position);

    /**
     * This method returns an {@link Iterator} on the list of subordinate for that {@code boss}. <br/>
     * <br/>
     * <b>BEWARE</b>: the iterator is readonly. You can modify the organization only through the
     * {@link MafiaOrganization#removeFromOrganization} method
     * 
     * @param boss
     * @return
     */
	Iterator<Mafioso> getSubordinates(Mafioso boss);

    /**
     * This method returns an {@link Iterator} on the list all mafioso in the organization. <br/>
     * <br/>
     * <b>BEWARE</b>: the iterator is readonly. You can modify the organization only through the
     * {@link MafiaOrganization#removeFromOrganization} method
     * 
     * @param boss
     * @return
     */
	Iterator<Mafioso> iterator();

}
