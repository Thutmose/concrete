package thutconcrete.api.energy;
 
import java.util.HashMap;
import java.util.Set;
 
import thutconcrete.api.energy.blocks.IConductor;
import thutconcrete.api.energy.blocks.ISink;
import thutconcrete.api.energy.blocks.ISource;
 
public class ElectricNetwork
{
        Set<ISource> sources;
        Set<IConductor> conductors;
        HashMap<ISink, Source> sinks;
        
        
        public static class Source
        {
                ISource source;
                IConductor weakest;
                double resistance;
               
                public Source(ISource source, IConductor conductor, double resistance)
                {
                        this.source = source;
                        this.weakest = conductor;
                        this.resistance = resistance;
                }
        }
}