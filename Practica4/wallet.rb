class Wallet
    attr_reader :from_wallet, :to_wallet

    def initialize(from_wallet, to_wallet)
        @from_wallet = from_wallet
        @to_wallet   = to_wallet
    end

    def self.setWallet(from, to)
        Wallet.new(from, to)
    end 
    
    def self.getWallet()
        return [from_wallet, to_wallet]
    end
end #class Wallet