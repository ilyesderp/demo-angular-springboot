/** Converts a decimal amount typed by a user (e.g. 12.5) to integer cents. */
export function toCents(amount: number): number {
  return Math.round(amount * 100);
}
