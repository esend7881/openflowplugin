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
import org.opendaylight.openflowplugin.openflow.md.util.ByteUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.Match;

public class MetadataEntrySerializer extends AbstractMatchEntrySerializer {

    @Override
    public void serialize(final Match match, final ByteBuf outBuffer) {
        super.serialize(match, outBuffer);
        // TODO: writeLong() should be faster
        outBuffer.writeBytes(ByteUtil.uint64toBytes(match.getMetadata().getMetadata()));

        if (getHasMask(match)) {
            // TODO: writeLong() should be faster
            writeMask(ByteUtil.uint64toBytes(match.getMetadata().getMetadataMask()),
                    outBuffer,
                    getValueLength());
        }
    }

    @Override
    public boolean matchTypeCheck(final Match match) {
        return match.getMetadata() != null;
    }

    @Override
    protected boolean getHasMask(final Match match) {
        return match.getMetadata().getMetadataMask() != null;
    }

    @Override
    protected int getOxmFieldCode() {
        return OxmMatchConstants.METADATA;
    }

    @Override
    protected int getOxmClassCode() {
        return OxmMatchConstants.OPENFLOW_BASIC_CLASS;
    }

    @Override
    protected int getValueLength() {
        return EncodeConstants.SIZE_OF_LONG_IN_BYTES;
    }
}
