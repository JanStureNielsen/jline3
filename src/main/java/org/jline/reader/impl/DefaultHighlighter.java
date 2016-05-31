/*
 * Copyright (c) 2002-2016, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.jline.reader.impl;

import org.jline.reader.LineReader;
import org.jline.reader.LineReader.RegionType;
import org.jline.reader.Highlighter;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.WCWidth;

public class DefaultHighlighter implements Highlighter {

    @Override
    public AttributedString highlight(LineReader reader, String buffer) {
        int underlineStart = -1;
        int underlineEnd = -1;
        int negativeStart = -1;
        int negativeEnd = -1;
        String search = reader.getSearchTerm();
        if (search != null && search.length() > 0) {
            underlineStart = buffer.indexOf(search);
            if (underlineStart >= 0) {
                underlineEnd = underlineStart + search.length() - 1;
            }
        }
        if (reader.getRegionActive() != RegionType.NONE) {
            negativeStart = reader.getRegionMark();
            negativeEnd = reader.getBuffer().cursor();
            if (negativeStart > negativeEnd) {
                int x = negativeEnd;
                negativeEnd = negativeStart;
                negativeStart = x;
            }
            if (reader.getRegionActive() == RegionType.LINE) {
                while (negativeStart > 0 && reader.getBuffer().atChar(negativeStart - 1) != '\n') {
                    negativeStart--;
                }
                while (negativeEnd < reader.getBuffer().length() - 1 && reader.getBuffer().atChar(negativeEnd + 1) != '\n') {
                    negativeEnd++;
                }
            }
        }

        AttributedStringBuilder sb = new AttributedStringBuilder();
        for (int i = 0; i < buffer.length(); i++) {
            if (i == underlineStart) {
                sb.style(sb.style().underline());
            }
            if (i == negativeStart) {
                sb.style(sb.style().inverse());
            }
            char c = buffer.charAt(i);
            if (c == '\t' || c == '\n') {
                sb.append(c);
            } else if (c < 32) {
                sb.style(sb.style().inverseNeg())
                        .append('^')
                        .append((char) (c + '@'))
                        .style(sb.style().inverseNeg());
            } else {
                int w = WCWidth.wcwidth(c);
                if (w > 0) {
                    sb.append(c);
                }
            }
            if (i == underlineEnd) {
                sb.style(sb.style().underlineOff());
            }
            if (i == negativeEnd) {
                sb.style(sb.style().inverseOff());
            }
        }
        return sb.toAttributedString();
    }

}
