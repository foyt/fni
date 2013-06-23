package fi.foyt.fni.materials;

/**
 * Describes material's archetype. 
 * 
 * Some material types are not unambiguous, this Enum qualifies what they are.
 */
public enum MaterialArchetype {
  
  FILE,
  PDF,
  FOLDER,
  IMAGE,
  VECTOR_IMAGE,
  DOCUMENT,
  PRESENTATION,
  SPREADSHEET
  
}
