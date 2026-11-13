
import { Search, X } from "lucide-react";

interface CustomerFiltersProps {
  search: string;
  onSearchChange: (value: string) => void;
}

export default function CustomerFilters({
  search,
  onSearchChange,
}: CustomerFiltersProps) {

  return (
    <div className="customer-filters">

      <div className="customer-search">

        <Search size={19} />

        <input
          type="text"
          placeholder="Search by name, customer ID or email..."
          value={search}
          onChange={(event) => onSearchChange(event.target.value)}
        />

        {search && (
          <button
            type="button"
            onClick={() => onSearchChange("")}
            aria-label="Clear search"
          >
            <X size={17} />
          </button>
        )}

      </div>

    </div>
  );
}
