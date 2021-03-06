/*
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowplugin.impl.protocol.serialization.match;

import io.netty.buffer.ByteBuf;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.openflowjava.protocol.api.util.OxmMatchConstants;
import org.opendaylight.openflowplugin.openflow.md.core.sal.convertor.common.IpConversionUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4MatchArbitraryBitMask;

public class Ipv4DestinationEntrySerializer extends AbstractMatchEntrySerializer {

    @Override
    public void serialize(Match match, ByteBuf outBuffer) {
        super.serialize(match, outBuffer);

        if (isPrefix(match)) {
            writeIpv4Prefix(((Ipv4Match) match.getLayer3Match()).getIpv4Destination(), outBuffer);
        } else if (isArbitrary(match)) {
            final Ipv4MatchArbitraryBitMask ipv4 = (Ipv4MatchArbitraryBitMask) match.getLayer3Match();
            writeIpv4Address(ipv4.getIpv4DestinationAddressNoMask(), outBuffer);

            if (getHasMask(match)) {
                writeMask(IpConversionUtil.convertArbitraryMaskToByteArray(ipv4.getIpv4DestinationArbitraryBitmask()),
                        outBuffer,
                        getValueLength());
            }
        }
    }

    @Override
    public boolean matchTypeCheck(Match match) {
        if (isPrefix(match)) {
            return match.getLayer3Match() != null
                    && ((Ipv4Match) match.getLayer3Match()).getIpv4Destination() != null;
        } else if (isArbitrary(match)) {
            return match.getLayer3Match() != null
                    && ((Ipv4MatchArbitraryBitMask) match.getLayer3Match()).getIpv4DestinationAddressNoMask() != null;
        }

        return false;
    }

    @Override
    protected boolean getHasMask(Match match) {
        if (isPrefix(match)) {
            return IpConversionUtil.hasIpv4Prefix(((Ipv4Match) match.getLayer3Match()).getIpv4Destination()) != null;
        } else if (isArbitrary(match)) {
            return ((Ipv4MatchArbitraryBitMask) match.getLayer3Match()).getIpv4DestinationArbitraryBitmask() != null;
        }

        return false;
    }

    private static boolean isPrefix(Match match) {
        return match.getLayer3Match() instanceof Ipv4Match;
    }

    private static boolean isArbitrary(Match match) {
        return match.getLayer3Match() instanceof Ipv4MatchArbitraryBitMask;
    }

    @Override
    protected int getOxmFieldCode() {
        return OxmMatchConstants.IPV4_DST;
    }

    @Override
    protected int getOxmClassCode() {
        return OxmMatchConstants.OPENFLOW_BASIC_CLASS;
    }

    @Override
    protected int getValueLength() {
        return EncodeConstants.SIZE_OF_INT_IN_BYTES;
    }
}
