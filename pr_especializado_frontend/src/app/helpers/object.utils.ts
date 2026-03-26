type OptimizedObjectType = { [key: string]: any };

export function getOptimizedObject<T extends OptimizedObjectType>(
  object: T
): T {
  const keys = Object.keys(object);
  for (const key of keys) {
    if (
      object[key] == undefined ||
      object[key] == null ||
      object[key] == '' ||
      object[key] == '0' ||
      object[key] == 0
    ) {
      delete object[key];
    }
  }
  return object;
}
