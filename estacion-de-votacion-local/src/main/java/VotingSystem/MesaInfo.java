//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.10
//
// <auto-generated>
//
// Generated from file `MainServer.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package VotingSystem;

public class MesaInfo implements java.lang.Cloneable,
                                 java.io.Serializable
{
    public int id;

    public String nombreColegio;

    public String direccion;

    public int numeroMesa;

    public String estado;

    public MesaInfo()
    {
        this.nombreColegio = "";
        this.direccion = "";
        this.estado = "";
    }

    public MesaInfo(int id, String nombreColegio, String direccion, int numeroMesa, String estado)
    {
        this.id = id;
        this.nombreColegio = nombreColegio;
        this.direccion = direccion;
        this.numeroMesa = numeroMesa;
        this.estado = estado;
    }

    public boolean equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        MesaInfo r = null;
        if(rhs instanceof MesaInfo)
        {
            r = (MesaInfo)rhs;
        }

        if(r != null)
        {
            if(this.id != r.id)
            {
                return false;
            }
            if(this.nombreColegio != r.nombreColegio)
            {
                if(this.nombreColegio == null || r.nombreColegio == null || !this.nombreColegio.equals(r.nombreColegio))
                {
                    return false;
                }
            }
            if(this.direccion != r.direccion)
            {
                if(this.direccion == null || r.direccion == null || !this.direccion.equals(r.direccion))
                {
                    return false;
                }
            }
            if(this.numeroMesa != r.numeroMesa)
            {
                return false;
            }
            if(this.estado != r.estado)
            {
                if(this.estado == null || r.estado == null || !this.estado.equals(r.estado))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public int hashCode()
    {
        int h_ = 5381;
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, "::VotingSystem::MesaInfo");
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, id);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, nombreColegio);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, direccion);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, numeroMesa);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, estado);
        return h_;
    }

    public MesaInfo clone()
    {
        MesaInfo c = null;
        try
        {
            c = (MesaInfo)super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return c;
    }

    public void ice_writeMembers(com.zeroc.Ice.OutputStream ostr)
    {
        ostr.writeInt(this.id);
        ostr.writeString(this.nombreColegio);
        ostr.writeString(this.direccion);
        ostr.writeInt(this.numeroMesa);
        ostr.writeString(this.estado);
    }

    public void ice_readMembers(com.zeroc.Ice.InputStream istr)
    {
        this.id = istr.readInt();
        this.nombreColegio = istr.readString();
        this.direccion = istr.readString();
        this.numeroMesa = istr.readInt();
        this.estado = istr.readString();
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, MesaInfo v)
    {
        if(v == null)
        {
            _nullMarshalValue.ice_writeMembers(ostr);
        }
        else
        {
            v.ice_writeMembers(ostr);
        }
    }

    static public MesaInfo ice_read(com.zeroc.Ice.InputStream istr)
    {
        MesaInfo v = new MesaInfo();
        v.ice_readMembers(istr);
        return v;
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, java.util.Optional<MesaInfo> v)
    {
        if(v != null && v.isPresent())
        {
            ice_write(ostr, tag, v.get());
        }
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, MesaInfo v)
    {
        if(ostr.writeOptional(tag, com.zeroc.Ice.OptionalFormat.FSize))
        {
            int pos = ostr.startSize();
            ice_write(ostr, v);
            ostr.endSize(pos);
        }
    }

    static public java.util.Optional<MesaInfo> ice_read(com.zeroc.Ice.InputStream istr, int tag)
    {
        if(istr.readOptional(tag, com.zeroc.Ice.OptionalFormat.FSize))
        {
            istr.skip(4);
            return java.util.Optional.of(MesaInfo.ice_read(istr));
        }
        else
        {
            return java.util.Optional.empty();
        }
    }

    private static final MesaInfo _nullMarshalValue = new MesaInfo();

    /** @hidden */
    public static final long serialVersionUID = 4006098205610611982L;
}
