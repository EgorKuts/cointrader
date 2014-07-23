package org.cryptocoinpartners.schema;

import org.cryptocoinpartners.util.Remainder;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;


/**
 * A Position represents an amount of some Asset within an Exchange.  If the Position is related to an Order
 * then the Position is being held in reserve (not tradeable) to cover the costs of the open Order.
 *
 * @author Tim Olson
 */
@Entity
public class Position extends Holding {


    public Position(Exchange exchange, Asset asset, Amount volume) {
        //volume.assertBasis(asset.getBasis());
        this.exchange = exchange;
        this.volumeCount = volume.toBasis(asset.getBasis(), Remainder.TO_HOUSE).getCount();
        this.asset = asset;
    }


    @Transient
    public Amount getVolume() {
        if( volume == null )
            volume = new DiscreteAmount(volumeCount, asset.getBasis());
        return volume;
    }


    /** If the SpecificOrder is not null, then this Position is being held in reserve as payment for that Order */
    @OneToOne
    @Nullable
    public SpecificOrder getOrder() { return order; }


    @Transient
    public boolean isReserved() { return order != null; }


    /**
     * Modifies this Position in-place by the amount of the position argument.
     * @param position a Position to add to this one.
     * @return true iff the positions both have the same Asset and the same Exchange, in which case this Position
     * has modified its volume by the amount in the position argument.
     */
    public boolean merge(Position position) {
        if( !exchange.equals(position.exchange) || !asset.equals(position.asset) )
            return false;
        volumeCount += position.volumeCount;
        return true;
    }

    public String toString() {
        return "Position=[Exchangee=" + exchange + ", qty=" + volumeCount
                        + ", side=" 
                        + ", entyDate=" 
                        + ", instrument=" + asset + "]";
}

    // JPA
    protected Position() { }


    protected long getVolumeCount() { return volumeCount; }
    protected void setVolumeCount(long volumeCount) { this.volumeCount = volumeCount; this.volume = null; }
    protected void setOrder(SpecificOrder order) { this.order = order; }


    private Amount volume;
    private long volumeCount;
    private SpecificOrder order;
}
