import { useState } from "react";
import Head from "next/head";
import Layout from "../components/Layout";
import StockList from "../components/StockList";
import Portfolio from "../components/Portfolio";
import TradeHistory from "../components/TradeHistory";
import TradingForm from "../components/TradingForm";

export default function Dashboard() {
  const [activeTab, setActiveTab] = useState<"stocks" | "portfolio" | "trades">(
    "stocks"
  );
  const [selectedStock, setSelectedStock] = useState<string | null>(null);
  const [showTradingForm, setShowTradingForm] = useState(false);
  const [tradeType, setTradeType] = useState<"BUY" | "SELL">("BUY");

  // Mock user ID for demo
  const userId = "demo-user";

  const handleTradeClick = (symbol: string, type: "BUY" | "SELL") => {
    setSelectedStock(symbol);
    setTradeType(type);
    setShowTradingForm(true);
  };

  const handleTradeComplete = () => {
    setShowTradingForm(false);
    setSelectedStock(null);
  };

  return (
    <>
      <Head>
        <title>Stock Trading Platform</title>
        <meta name="description" content="A modern stock trading platform" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <Layout>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900">
              Stock Trading Dashboard
            </h1>
            <p className="mt-2 text-gray-600">
              Monitor stocks, manage your portfolio, and execute trades
            </p>
          </div>

          {/* Navigation Tabs */}
          <div className="border-b border-gray-200 mb-6">
            <nav className="-mb-px flex space-x-8">
              {[
                { id: "stocks", label: "Market Data", icon: "📈" },
                { id: "portfolio", label: "Portfolio", icon: "💼" },
                { id: "trades", label: "Trade History", icon: "📋" },
              ].map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`
                    flex items-center py-2 px-1 border-b-2 font-medium text-sm
                    ${
                      activeTab === tab.id
                        ? "border-primary-500 text-primary-600"
                        : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
                    }
                  `}
                >
                  <span className="mr-2">{tab.icon}</span>
                  {tab.label}
                </button>
              ))}
            </nav>
          </div>

          {/* Content */}
          <div className="animate-fade-in">
            {activeTab === "stocks" && (
              <StockList onTradeClick={handleTradeClick} />
            )}
            {activeTab === "portfolio" && (
              <Portfolio userId={userId} onTradeClick={handleTradeClick} />
            )}
            {activeTab === "trades" && <TradeHistory userId={userId} />}
          </div>

          {/* Trading Form Modal */}
          <TradingForm
            isOpen={showTradingForm}
            userId={userId}
            stockSymbol={selectedStock || ""}
            tradeType={tradeType}
            onClose={() => setShowTradingForm(false)}
          />
        </div>
      </Layout>
    </>
  );
}
