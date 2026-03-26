export function formatNameElection(input: string){
  if (!input) return '';
  return input.toLowerCase().includes('parlamento andino') ? 'Parlamento Andino' : input.charAt(0).toUpperCase() + input.slice(1).toLowerCase();
}

// Keep old name for compatibility
export { formatNameElection as formatNameElectionOld };
