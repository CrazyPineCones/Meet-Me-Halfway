
import { useState } from "react";
import "./App.css";
import {
  CarFront,
  Footprints,
  Clapperboard,
  Bike,
  Globe,
  Clock,
  Plus,
  X,
} from "lucide-react";

type TransportMode = "DRIVING" | "WALKING";
type RankingPreference = "GEOGRAPHIC" | "TIME";

type LocationInput = {
  id: number;
  name: string;
  address: string;
  mode: TransportMode;
};

type TravelTime = {
  address: string;
  minutes: number;
};

type Theater = {
  name: string;
  averageMinutes: number;
  distanceFromMidpointMiles: number;
  travelTimes: TravelTime[];
};

type MeetResponse = {
  theaters: Theater[];
};

function App() {
  const [locations, setLocations] = useState<LocationInput[]>([
    { id: 1, name: "", address: "", mode: "DRIVING" },
    { id: 2, name: "", address: "", mode: "DRIVING" },
  ]);

  const [rankingPreference, setRankingPreference] =
    useState<RankingPreference>("GEOGRAPHIC");

  const [theaters, setTheaters] = useState<Theater[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const [submittedLocations, setSubmittedLocations] =
    useState<LocationInput[]>([]);

  const [hasSearched, setHasSearched] = useState(false);

  const addLocation = () => {
    setLocations([
      ...locations,
      {
        id: Date.now(),
        name: "",
        address: "",
        mode: "DRIVING",
      },
    ]);
  };

  const removeLocation = (id: number) => {
    if (locations.length <= 2) return;
    setLocations(locations.filter((location) => location.id !== id));
  };

  const updateLocation = (
    id: number,
    field: "name" | "address" | "mode",
    value: string
  ) => {
    setLocations((currentLocations) =>
      currentLocations.map((location) =>
        location.id === id ? { ...location, [field]: value } : location
      )
    );
  };

  const handleSubmit = async () => {
    setError("");
    setTheaters([]);

    // Make sure every participant has entered an address.
    if (locations.some((location) => !location.address.trim())) {
      setError("Please enter an address for everyone.");
      return;
    }
    setHasSearched(false);
    setSubmittedLocations(locations.map((location) => ({ ...location })));

    setIsLoading(true);

    const requestBody = {
      locations: locations.map((location) => ({
        address: location.address.trim(),
        mode: location.mode,
      })),
      rankingPreference,
    };

    try {
      const response = await fetch("http://localhost:8080/api/meet", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestBody),
      });

      const responseText = await response.text();

      if (!response.ok) {
        throw new Error(responseText || "Something went wrong.");
      }

      const data: MeetResponse = JSON.parse(responseText);

      setTheaters(data.theaters ?? []);
      setHasSearched(true);

      console.log("Backend response:", data);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Unable to connect to the server."
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="app">
      <header className="app-header">
        <div className="title-row">
          <h1>Meet Me Halfway</h1>
          <Clapperboard size={32} strokeWidth={1.8} />
        </div>
        <p>Find the perfect meeting place between everyone.</p>
      </header>

      <section className="locations">

        {locations.map((location, index) => (
          <div className="location-card" key={location.id}>
            <div className="person-heading">
              <h3>{location.name.trim() || `Person ${index + 1}`}</h3>

              {locations.length > 2 && (
                <button
                  type="button"
                  className="remove-person"
                  title="Remove person"
                  onClick={() => removeLocation(location.id)}
                >
                  <X size={18} />
                </button>
              )}
            </div>

            <input
              className="name-input"
              type="text"
              placeholder="Enter a name"
              value={location.name}
              onChange={(e) =>
                updateLocation(location.id, "name", e.target.value)
              }
            />

            <div className="location-input-row">
              <input
                type="text"
                placeholder="Enter an address"
                value={location.address}
                onChange={(e) =>
                  updateLocation(location.id, "address", e.target.value)
                }
              />

              <div className="transport-options">
                <button
                  type="button"
                  title="Driving"
                  className={location.mode === "DRIVING" ? "selected" : ""}
                  onClick={() => updateLocation(location.id, "mode", "DRIVING")}
                >
                  <CarFront size={23} />
                </button>

                <button
                  type="button"
                  title="Walking"
                  className={location.mode === "WALKING" ? "selected" : ""}
                  onClick={() => updateLocation(location.id, "mode", "WALKING")}
                >
                  <Footprints size={23} />
                </button>

                <button
                  type="button"
                  title="Biking (coming soon)"
                  disabled
                  className="coming-soon"
                >
                  <Bike size={23} />
                </button>
              </div>
            </div>
          </div>
        ))}

        <div className="add-person-container">
          <button
            type="button"
            className="add-person-button"
            onClick={addLocation}
          >
            <Plus size={18} />
            Add another person
          </button>
        </div>

        <div className="location-card utility-card">
          <h2>Ranking preference</h2>
          <p>How should we decide what's halfway?</p>

          <div className="ranking-options">
            <button
              type="button"
              className={rankingPreference === "GEOGRAPHIC" ? "selected" : ""}
              onClick={() => setRankingPreference("GEOGRAPHIC")}
            >
              <Globe size={22} />
              Geographic
            </button>

            <button
              type="button"
              className={rankingPreference === "TIME" ? "selected" : ""}
              onClick={() => setRankingPreference("TIME")}
            >
              <Clock size={22} />
              Travel time
            </button>
          </div>
        </div>

        <div className="location-card utility-card">
          <button
            type="button"
            className="meet-button"
            onClick={handleSubmit}
          >
            <Clapperboard size={25} />
            Meet Me Halfway!
          </button>
          {isLoading && <p>Halfway There...</p>}

          {error && <p className="error-message">{error}</p>}
        </div>
      </section>

      <section className="results-section">
        {isLoading && (
          <div className="results-loading">
            <Clapperboard size={28} />
            <h2>Finding your halfway point...</h2>
            <p>Checking nearby theaters and travel times.</p>
          </div>
        )}

        {hasSearched && !isLoading && theaters.length > 0 && (
          <>
            <h2>Your meeting spots</h2>
            <p className="results-subtitle">
              Here are the theaters ranked by your preference.
            </p>

            <div className="theater-list">
              {theaters.map((theater, index) => (
                <article className="theater-card" key={`${theater.name}-${index}`}>
                  <div className="theater-heading">
                    <h3>{theater.name}</h3>
                    <span className="theater-rank">#{index + 1}</span>
                  </div>

                  <div className="theater-stats">
                    <div>
                      <span className="stat-label">Average travel time</span>
                      <strong>{theater.averageMinutes.toFixed(1)} min</strong>
                    </div>

                    <div>
                      <span className="stat-label">From midpoint</span>
                      <strong>{theater.distanceFromMidpointMiles.toFixed(1)} mi</strong>
                    </div>
                  </div>

                  <div className="individual-times">
                    <h4>Travel times</h4>

                    {theater.travelTimes.map((travelTime, personIndex) => {
                      const person = submittedLocations[personIndex];

                      return (
                        <div
                          className="person-travel-time"
                          key={`${travelTime.address}-${personIndex}`}
                        >
                          <span>
                            {person?.name.trim() || `Person ${personIndex + 1}`}
                          </span>
                          <strong>{travelTime.minutes.toFixed(1)} min</strong>
                        </div>
                      );
                    })}
                  </div>
                </article>
              ))}
            </div>
          </>
        )}

        {hasSearched && !isLoading && theaters.length === 0 && (
          <div className="no-results">
            <h2>No theaters found</h2>
            <p>
              We couldn't find any theaters that meet your travel-time
              requirements. Try increasing your maximum travel time.
            </p>
          </div>
        )}
      </section>

    </main>
  );
}


export default App;
