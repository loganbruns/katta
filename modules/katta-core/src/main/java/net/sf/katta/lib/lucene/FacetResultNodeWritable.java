/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.katta.lib.lucene;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.hadoop.io.Writable;
import org.apache.lucene.facet.search.results.FacetResultNode;
import org.apache.lucene.facet.search.results.MutableFacetResultNode;

public class FacetResultNodeWritable implements Comparable, Writable {

  private FacetResultNode _facetResultNode;

  public FacetResultNodeWritable() {
    // for serialization
  }

  public FacetResultNodeWritable(FacetResultNode facetResultNode) {
    _facetResultNode = facetResultNode;
  }

  @Override
  public void readFields(DataInput input) throws IOException {
    int ordinal = input.readInt();
    double value = input.readDouble();
    double residue = input.readDouble();
    CategoryPathWritable label = new CategoryPathWritable();
    label.readFields(input);
    
    _facetResultNode = new MutableFacetResultNode(ordinal,
                                                  value,
                                                  residue,
                                                  label.getCategoryPath(),
                                                  null);
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.writeInt(_facetResultNode.getOrdinal());
    output.writeDouble(_facetResultNode.getValue());
    output.writeDouble(_facetResultNode.getResidue());
    CategoryPathWritable label = new CategoryPathWritable(_facetResultNode.getLabel());
    label.write(output);
  }

  public FacetResultNode getFacetResultNode() {
    return _facetResultNode;
  }

  @Override
  public boolean equals(Object obj) {
    if (getClass() != obj.getClass()) {
      return false;
    }
    FacetResultNode other = ((FacetResultNodeWritable) obj).getFacetResultNode();
    return _facetResultNode.equals(other);
  }

  @Override
  public int compareTo(Object obj) {
    if (getClass() != obj.getClass())
      throw new ClassCastException();

    // Sort by facet name then value descending

    FacetResultNode other = ((FacetResultNodeWritable) obj).getFacetResultNode();
    int cmp = _facetResultNode.getLabel().getComponent(0).compareTo(other.getLabel().getComponent(0));
    if (cmp != 0)
      return cmp;

    return (int) (other.getValue() - _facetResultNode.getValue());
  }

  @Override
  public String toString() {
    return _facetResultNode != null ? _facetResultNode.toString() : "null";
  }

  public static FacetResultNodeWritable[] toArray(FacetResultNode[] facetResultNodes) {
    int l = facetResultNodes.length;
    FacetResultNodeWritable[] paths = new FacetResultNodeWritable[l];
    for (int i=0; i<l; ++i)
      paths[i] = new FacetResultNodeWritable(facetResultNodes[i]);

    return paths;
  }

}
