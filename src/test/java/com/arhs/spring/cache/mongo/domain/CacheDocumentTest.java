/**
 * MIT License
 *
 * Copyright (c) 2016 ARHS Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.arhs.spring.cache.mongo.domain;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;

/**
 * Unit tests for {@link CacheDocument}.
 *
 * @author ARHS Spikeseed
 */
public class CacheDocumentTest {

    /**
     * Test for {@link CacheDocument())}
     */
    @Test
    public void constructor() {
        CacheDocument document = new CacheDocument();
        Assert.assertNull(document.getId());
        Assert.assertNull(document.getElement());
        Assert.assertNull(document.getCreationDate());

        document = new CacheDocument("id", "element");
        Assert.assertEquals("id", document.getId());
        Assert.assertEquals("element", document.getElement());
        Assert.assertNotNull(document.getCreationDate());

        final Instant creationDate = Instant.now();
        document = new CacheDocument("id", "element", creationDate);
        Assert.assertEquals("id", document.getId());
        Assert.assertEquals("element", document.getElement());
        Assert.assertEquals(creationDate, document.getCreationDate());
    }

    /**
     * Test for {@link CacheDocument#setCreationDate(Instant))}
     */
    @Test
    public void setCreationDate() {
        final Instant creationDate = Instant.now();
        final CacheDocument document = new CacheDocument();
        Assert.assertNull(document.getCreationDate());

        document.setCreationDate(creationDate);
        Assert.assertEquals(creationDate, document.getCreationDate());
    }

    /**
     * Test for {@link CacheDocument#setId(String))}
     */
    @Test
    public void setId() {
        final CacheDocument document = new CacheDocument();
        Assert.assertNull(document.getId());

        document.setId("id");
        Assert.assertEquals("id", document.getId());
    }

    /**
     * Test for {@link CacheDocument#setElement(String))}
     */
    @Test
    public void setElement() {
        final CacheDocument document = new CacheDocument();
        Assert.assertNull(document.getElement());

        document.setElement("element");
        Assert.assertEquals("element", document.getElement());
    }

}
