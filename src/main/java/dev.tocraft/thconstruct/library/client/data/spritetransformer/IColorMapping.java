package dev.tocraft.thconstruct.library.client.data.spritetransformer;


import dev.tocraft.eomantle.data.gson.GenericRegisteredSerializer;
import dev.tocraft.eomantle.data.gson.GenericRegisteredSerializer.IJsonSerializable;

/** Logic to map a color to another color */
public interface IColorMapping extends IJsonSerializable {
  /** Serializer used for this transformer, can register your deserializers with it */
  GenericRegisteredSerializer<IColorMapping> SERIALIZER = new GenericRegisteredSerializer<>();

  /**
   * Maps the given color
   * @param color  Input color in AABBGGRR format
   * @return New color in AABBGGRR format
   */
  int mapColor(int color);
}
