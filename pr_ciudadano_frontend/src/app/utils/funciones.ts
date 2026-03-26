export function formatNumberWithApostrophe(numberValue: number | null): string {
    if (numberValue == null) return '0';
    
    const formatted = numberValue.toLocaleString('en-US', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 20
    });
    
    return formatted
      .replace(/,(\d{3},\d{3})/g, "'$1")
      .replace(/,(\d{6})/g, "'$1");
}