//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.10
//
// <auto-generated>
//
// Generated from file `ProxyCacheDBCiudad.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package VotingSystem;

public interface ProxyCacheDBCiudad extends com.zeroc.Ice.Object
{
    Votante ConsultarVotantePorCedula(String cedula, com.zeroc.Ice.Current current);

    Candidato[] ConsultarCandidatos(com.zeroc.Ice.Current current);

    Zona[] GetZonasVotacion(com.zeroc.Ice.Current current);

    Zona ZonaMesaAsignada(String cedula, com.zeroc.Ice.Current current);

    int IDZonaVotacion(String cedula, com.zeroc.Ice.Current current);

    int GetConteoVotos(int mesaId, com.zeroc.Ice.Current current);

    boolean AgregarVoto(Voto voto, com.zeroc.Ice.Current current);

    boolean AgregarSospechoso(String cedula, String motivo, com.zeroc.Ice.Current current);

    boolean RegistrarLogs(LogEntry log, com.zeroc.Ice.Current current);

    int GetConteoVotosPorCandidato(int candidatoId, com.zeroc.Ice.Current current);

    String ConsultarMesaDescriptiva(String cedula, com.zeroc.Ice.Current current);

    boolean YaVoto(String cedula, com.zeroc.Ice.Current current);

    boolean EsSospechoso(String cedula, com.zeroc.Ice.Current current);

    boolean AgregarLoteVotos(Voto[] lote, com.zeroc.Ice.Current current);

    /** @hidden */
    static final String[] _iceIds =
    {
        "::Ice::Object",
        "::VotingSystem::ProxyCacheDBCiudad"
    };

    @Override
    default String[] ice_ids(com.zeroc.Ice.Current current)
    {
        return _iceIds;
    }

    @Override
    default String ice_id(com.zeroc.Ice.Current current)
    {
        return ice_staticId();
    }

    static String ice_staticId()
    {
        return "::VotingSystem::ProxyCacheDBCiudad";
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_ConsultarVotantePorCedula(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_cedula;
        iceP_cedula = istr.readString();
        inS.endReadParams();
        Votante ret = obj.ConsultarVotantePorCedula(iceP_cedula, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        Votante.ice_write(ostr, ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_ConsultarCandidatos(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        inS.readEmptyParams();
        Candidato[] ret = obj.ConsultarCandidatos(current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        CandidatoSeqHelper.write(ostr, ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_GetZonasVotacion(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        inS.readEmptyParams();
        Zona[] ret = obj.GetZonasVotacion(current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ZonaSeqHelper.write(ostr, ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_ZonaMesaAsignada(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_cedula;
        iceP_cedula = istr.readString();
        inS.endReadParams();
        Zona ret = obj.ZonaMesaAsignada(iceP_cedula, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        Zona.ice_write(ostr, ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_IDZonaVotacion(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_cedula;
        iceP_cedula = istr.readString();
        inS.endReadParams();
        int ret = obj.IDZonaVotacion(iceP_cedula, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeInt(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_GetConteoVotos(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        int iceP_mesaId;
        iceP_mesaId = istr.readInt();
        inS.endReadParams();
        int ret = obj.GetConteoVotos(iceP_mesaId, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeInt(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_AgregarVoto(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        Voto iceP_voto;
        iceP_voto = Voto.ice_read(istr);
        inS.endReadParams();
        boolean ret = obj.AgregarVoto(iceP_voto, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeBool(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_AgregarSospechoso(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_cedula;
        String iceP_motivo;
        iceP_cedula = istr.readString();
        iceP_motivo = istr.readString();
        inS.endReadParams();
        boolean ret = obj.AgregarSospechoso(iceP_cedula, iceP_motivo, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeBool(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_RegistrarLogs(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        LogEntry iceP_log;
        iceP_log = LogEntry.ice_read(istr);
        inS.endReadParams();
        boolean ret = obj.RegistrarLogs(iceP_log, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeBool(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_GetConteoVotosPorCandidato(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        int iceP_candidatoId;
        iceP_candidatoId = istr.readInt();
        inS.endReadParams();
        int ret = obj.GetConteoVotosPorCandidato(iceP_candidatoId, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeInt(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_ConsultarMesaDescriptiva(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_cedula;
        iceP_cedula = istr.readString();
        inS.endReadParams();
        String ret = obj.ConsultarMesaDescriptiva(iceP_cedula, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeString(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_YaVoto(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_cedula;
        iceP_cedula = istr.readString();
        inS.endReadParams();
        boolean ret = obj.YaVoto(iceP_cedula, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeBool(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_EsSospechoso(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_cedula;
        iceP_cedula = istr.readString();
        inS.endReadParams();
        boolean ret = obj.EsSospechoso(iceP_cedula, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeBool(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_AgregarLoteVotos(ProxyCacheDBCiudad obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        Voto[] iceP_lote;
        iceP_lote = VotoSeqHelper.read(istr);
        inS.endReadParams();
        boolean ret = obj.AgregarLoteVotos(iceP_lote, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ostr.writeBool(ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /** @hidden */
    final static String[] _iceOps =
    {
        "AgregarLoteVotos",
        "AgregarSospechoso",
        "AgregarVoto",
        "ConsultarCandidatos",
        "ConsultarMesaDescriptiva",
        "ConsultarVotantePorCedula",
        "EsSospechoso",
        "GetConteoVotos",
        "GetConteoVotosPorCandidato",
        "GetZonasVotacion",
        "IDZonaVotacion",
        "RegistrarLogs",
        "YaVoto",
        "ZonaMesaAsignada",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping"
    };

    /** @hidden */
    @Override
    default java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceDispatch(com.zeroc.IceInternal.Incoming in, com.zeroc.Ice.Current current)
        throws com.zeroc.Ice.UserException
    {
        int pos = java.util.Arrays.binarySearch(_iceOps, current.operation);
        if(pos < 0)
        {
            throw new com.zeroc.Ice.OperationNotExistException(current.id, current.facet, current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return _iceD_AgregarLoteVotos(this, in, current);
            }
            case 1:
            {
                return _iceD_AgregarSospechoso(this, in, current);
            }
            case 2:
            {
                return _iceD_AgregarVoto(this, in, current);
            }
            case 3:
            {
                return _iceD_ConsultarCandidatos(this, in, current);
            }
            case 4:
            {
                return _iceD_ConsultarMesaDescriptiva(this, in, current);
            }
            case 5:
            {
                return _iceD_ConsultarVotantePorCedula(this, in, current);
            }
            case 6:
            {
                return _iceD_EsSospechoso(this, in, current);
            }
            case 7:
            {
                return _iceD_GetConteoVotos(this, in, current);
            }
            case 8:
            {
                return _iceD_GetConteoVotosPorCandidato(this, in, current);
            }
            case 9:
            {
                return _iceD_GetZonasVotacion(this, in, current);
            }
            case 10:
            {
                return _iceD_IDZonaVotacion(this, in, current);
            }
            case 11:
            {
                return _iceD_RegistrarLogs(this, in, current);
            }
            case 12:
            {
                return _iceD_YaVoto(this, in, current);
            }
            case 13:
            {
                return _iceD_ZonaMesaAsignada(this, in, current);
            }
            case 14:
            {
                return com.zeroc.Ice.Object._iceD_ice_id(this, in, current);
            }
            case 15:
            {
                return com.zeroc.Ice.Object._iceD_ice_ids(this, in, current);
            }
            case 16:
            {
                return com.zeroc.Ice.Object._iceD_ice_isA(this, in, current);
            }
            case 17:
            {
                return com.zeroc.Ice.Object._iceD_ice_ping(this, in, current);
            }
        }

        assert(false);
        throw new com.zeroc.Ice.OperationNotExistException(current.id, current.facet, current.operation);
    }
}
