export default function NumberStepper({
  value,
  onChange,
  min = 0,
  max = 999,
  step = 1,
  placeholder = '',
  ariaLabel = 'Valor',
  allowEmpty = true,
}) {
  const num = Number(value);
  const hasValue = value !== '' && value !== null && value !== undefined && !Number.isNaN(num);

  const clamp = n => Math.min(max, Math.max(min, n));

  const setValue = next => {
    onChange(clamp(next));
  };

  const handleInput = e => {
    const raw = e.target.value.replace(/[^\d]/g, '');

    if (!raw) {
      onChange(allowEmpty ? '' : min);
      return;
    }

    onChange(clamp(Number(raw)));
  };

  return (
    <div className="number-stepper">
      <button
        type="button"
        className="number-stepper-btn"
        disabled={hasValue ? num <= min : true}
        onClick={() => setValue((hasValue ? num : min) - step)}
        aria-label={`Diminuir ${ariaLabel}`}
      >
        −
      </button>

      <input
        className="number-stepper-input"
        value={value}
        onChange={handleInput}
        placeholder={placeholder}
        inputMode="numeric"
        aria-label={ariaLabel}
      />

      <button
        type="button"
        className="number-stepper-btn"
        disabled={hasValue ? num >= max : false}
        onClick={() => setValue((hasValue ? num : min) + step)}
        aria-label={`Aumentar ${ariaLabel}`}
      >
        +
      </button>
    </div>
  );
}