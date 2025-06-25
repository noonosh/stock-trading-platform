import { useQuery } from "@tanstack/react-query";
import { TrendingUp, TrendingDown, DollarSign } from "lucide-react";
import { portfolioApi } from "../services/api";
import { Portfolio as PortfolioType } from "../types";

interface PortfolioProps {
  userId: string;
  onTradeClick: (symbol: string, type: "BUY" | "SELL") => void;
}

export default function Portfolio({ userId, onTradeClick }: PortfolioProps) {
  const {
    data: portfolio,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["portfolio", userId],
    queryFn: () => portfolioApi.getUserPortfolio(userId),
    refetchInterval: 30000,
  });

  const { data: summary } = useQuery({
    queryKey: ["portfolio-summary", userId],
    queryFn: () => portfolioApi.getPortfolioSummary(userId),
    refetchInterval: 30000,
  });

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(price);
  };

  const formatPercentage = (percentage: number) => {
    const sign = percentage >= 0 ? "+" : "";
    return `${sign}${percentage.toFixed(2)}%`;
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card">
        <div className="card-body text-center">
          <p className="text-red-600">
            Error loading portfolio. Please try again later.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Portfolio Summary */}
      {summary && (
        <div className="grid gap-4 md:grid-cols-4">
          <div className="card">
            <div className="card-body">
              <div className="flex items-center">
                <DollarSign className="h-8 w-8 text-primary-600" />
                <div className="ml-3">
                  <p className="text-sm font-medium text-gray-500">
                    Total Value
                  </p>
                  <p className="text-2xl font-bold text-gray-900">
                    {formatPrice(summary.totalValue)}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="card-body">
              <div className="flex items-center">
                <DollarSign className="h-8 w-8 text-gray-400" />
                <div className="ml-3">
                  <p className="text-sm font-medium text-gray-500">
                    Total Cost
                  </p>
                  <p className="text-2xl font-bold text-gray-900">
                    {formatPrice(summary.totalCost)}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="card-body">
              <div className="flex items-center">
                {summary.totalGainLoss >= 0 ? (
                  <TrendingUp className="h-8 w-8 text-success-600" />
                ) : (
                  <TrendingDown className="h-8 w-8 text-danger-600" />
                )}
                <div className="ml-3">
                  <p className="text-sm font-medium text-gray-500">Gain/Loss</p>
                  <p
                    className={`text-2xl font-bold ${
                      summary.totalGainLoss >= 0
                        ? "text-success-600"
                        : "text-danger-600"
                    }`}
                  >
                    {formatPrice(summary.totalGainLoss)}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="card">
            <div className="card-body">
              <div className="flex items-center">
                {summary.totalGainLossPercentage >= 0 ? (
                  <TrendingUp className="h-8 w-8 text-success-600" />
                ) : (
                  <TrendingDown className="h-8 w-8 text-danger-600" />
                )}
                <div className="ml-3">
                  <p className="text-sm font-medium text-gray-500">Return %</p>
                  <p
                    className={`text-2xl font-bold ${
                      summary.totalGainLossPercentage >= 0
                        ? "text-success-600"
                        : "text-danger-600"
                    }`}
                  >
                    {formatPercentage(summary.totalGainLossPercentage)}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Holdings Table */}
      <div className="card">
        <div className="card-header">
          <h2 className="text-xl font-semibold text-gray-900">Holdings</h2>
        </div>
        <div className="card-body p-0">
          {portfolio && portfolio.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="table">
                <thead className="table-header">
                  <tr>
                    <th className="table-header-cell">Symbol</th>
                    <th className="table-header-cell">Quantity</th>
                    <th className="table-header-cell">Avg Cost</th>
                    <th className="table-header-cell">Current Price</th>
                    <th className="table-header-cell">Market Value</th>
                    <th className="table-header-cell">Gain/Loss</th>
                    <th className="table-header-cell">Return %</th>
                    <th className="table-header-cell">Actions</th>
                  </tr>
                </thead>
                <tbody className="table-body">
                  {portfolio.map((holding) => (
                    <tr key={holding.id}>
                      <td className="table-cell font-medium">
                        {holding.stockSymbol}
                      </td>
                      <td className="table-cell">{holding.quantity}</td>
                      <td className="table-cell">
                        {formatPrice(holding.averagePurchasePrice)}
                      </td>
                      <td className="table-cell">
                        {formatPrice(holding.currentPrice)}
                      </td>
                      <td className="table-cell">
                        {formatPrice(holding.totalValue)}
                      </td>
                      <td
                        className={`table-cell ${
                          holding.gainLoss >= 0
                            ? "text-success-600"
                            : "text-danger-600"
                        }`}
                      >
                        {formatPrice(holding.gainLoss)}
                      </td>
                      <td
                        className={`table-cell ${
                          holding.gainLossPercentage >= 0
                            ? "text-success-600"
                            : "text-danger-600"
                        }`}
                      >
                        {formatPercentage(holding.gainLossPercentage)}
                      </td>
                      <td className="table-cell">
                        <div className="flex space-x-2">
                          <button
                            onClick={() =>
                              onTradeClick(holding.stockSymbol, "BUY")
                            }
                            className="btn-success text-xs"
                          >
                            Buy
                          </button>
                          <button
                            onClick={() =>
                              onTradeClick(holding.stockSymbol, "SELL")
                            }
                            className="btn-danger text-xs"
                          >
                            Sell
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="p-6 text-center">
              <p className="text-gray-500">
                No holdings yet. Start trading to see your portfolio here.
              </p>
              <button
                onClick={() => window.location.reload()}
                className="btn-primary mt-4"
              >
                View Market
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
