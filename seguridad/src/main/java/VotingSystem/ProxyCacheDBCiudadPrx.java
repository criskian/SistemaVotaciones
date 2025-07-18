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

public interface ProxyCacheDBCiudadPrx extends com.zeroc.Ice.ObjectPrx
{
    default Votante ConsultarVotantePorCedula(String cedula)
    {
        return ConsultarVotantePorCedula(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default Votante ConsultarVotantePorCedula(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_ConsultarVotantePorCedulaAsync(cedula, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<Votante> ConsultarVotantePorCedulaAsync(String cedula)
    {
        return _iceI_ConsultarVotantePorCedulaAsync(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Votante> ConsultarVotantePorCedulaAsync(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_ConsultarVotantePorCedulaAsync(cedula, context, false);
    }

    /**
     * @hidden
     * @param iceP_cedula -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Votante> _iceI_ConsultarVotantePorCedulaAsync(String iceP_cedula, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Votante> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "ConsultarVotantePorCedula", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_cedula);
                 }, istr -> {
                     Votante ret;
                     ret = Votante.ice_read(istr);
                     return ret;
                 });
        return f;
    }

    default Candidato[] ConsultarCandidatos()
    {
        return ConsultarCandidatos(com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default Candidato[] ConsultarCandidatos(java.util.Map<String, String> context)
    {
        return _iceI_ConsultarCandidatosAsync(context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<Candidato[]> ConsultarCandidatosAsync()
    {
        return _iceI_ConsultarCandidatosAsync(com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Candidato[]> ConsultarCandidatosAsync(java.util.Map<String, String> context)
    {
        return _iceI_ConsultarCandidatosAsync(context, false);
    }

    /**
     * @hidden
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Candidato[]> _iceI_ConsultarCandidatosAsync(java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Candidato[]> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "ConsultarCandidatos", null, sync, null);
        f.invoke(true, context, null, null, istr -> {
                     Candidato[] ret;
                     ret = CandidatoSeqHelper.read(istr);
                     return ret;
                 });
        return f;
    }

    default Zona[] GetZonasVotacion()
    {
        return GetZonasVotacion(com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default Zona[] GetZonasVotacion(java.util.Map<String, String> context)
    {
        return _iceI_GetZonasVotacionAsync(context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<Zona[]> GetZonasVotacionAsync()
    {
        return _iceI_GetZonasVotacionAsync(com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Zona[]> GetZonasVotacionAsync(java.util.Map<String, String> context)
    {
        return _iceI_GetZonasVotacionAsync(context, false);
    }

    /**
     * @hidden
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Zona[]> _iceI_GetZonasVotacionAsync(java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Zona[]> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "GetZonasVotacion", null, sync, null);
        f.invoke(true, context, null, null, istr -> {
                     Zona[] ret;
                     ret = ZonaSeqHelper.read(istr);
                     return ret;
                 });
        return f;
    }

    default Zona ZonaMesaAsignada(String cedula)
    {
        return ZonaMesaAsignada(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default Zona ZonaMesaAsignada(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_ZonaMesaAsignadaAsync(cedula, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<Zona> ZonaMesaAsignadaAsync(String cedula)
    {
        return _iceI_ZonaMesaAsignadaAsync(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Zona> ZonaMesaAsignadaAsync(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_ZonaMesaAsignadaAsync(cedula, context, false);
    }

    /**
     * @hidden
     * @param iceP_cedula -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Zona> _iceI_ZonaMesaAsignadaAsync(String iceP_cedula, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Zona> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "ZonaMesaAsignada", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_cedula);
                 }, istr -> {
                     Zona ret;
                     ret = Zona.ice_read(istr);
                     return ret;
                 });
        return f;
    }

    default int IDZonaVotacion(String cedula)
    {
        return IDZonaVotacion(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default int IDZonaVotacion(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_IDZonaVotacionAsync(cedula, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Integer> IDZonaVotacionAsync(String cedula)
    {
        return _iceI_IDZonaVotacionAsync(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Integer> IDZonaVotacionAsync(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_IDZonaVotacionAsync(cedula, context, false);
    }

    /**
     * @hidden
     * @param iceP_cedula -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Integer> _iceI_IDZonaVotacionAsync(String iceP_cedula, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Integer> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "IDZonaVotacion", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_cedula);
                 }, istr -> {
                     int ret;
                     ret = istr.readInt();
                     return ret;
                 });
        return f;
    }

    default int GetConteoVotos(int mesaId)
    {
        return GetConteoVotos(mesaId, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default int GetConteoVotos(int mesaId, java.util.Map<String, String> context)
    {
        return _iceI_GetConteoVotosAsync(mesaId, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Integer> GetConteoVotosAsync(int mesaId)
    {
        return _iceI_GetConteoVotosAsync(mesaId, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Integer> GetConteoVotosAsync(int mesaId, java.util.Map<String, String> context)
    {
        return _iceI_GetConteoVotosAsync(mesaId, context, false);
    }

    /**
     * @hidden
     * @param iceP_mesaId -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Integer> _iceI_GetConteoVotosAsync(int iceP_mesaId, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Integer> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "GetConteoVotos", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeInt(iceP_mesaId);
                 }, istr -> {
                     int ret;
                     ret = istr.readInt();
                     return ret;
                 });
        return f;
    }

    default boolean AgregarVoto(Voto voto)
    {
        return AgregarVoto(voto, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean AgregarVoto(Voto voto, java.util.Map<String, String> context)
    {
        return _iceI_AgregarVotoAsync(voto, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> AgregarVotoAsync(Voto voto)
    {
        return _iceI_AgregarVotoAsync(voto, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> AgregarVotoAsync(Voto voto, java.util.Map<String, String> context)
    {
        return _iceI_AgregarVotoAsync(voto, context, false);
    }

    /**
     * @hidden
     * @param iceP_voto -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_AgregarVotoAsync(Voto iceP_voto, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "AgregarVoto", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     Voto.ice_write(ostr, iceP_voto);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    default boolean AgregarSospechoso(String cedula, String motivo)
    {
        return AgregarSospechoso(cedula, motivo, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean AgregarSospechoso(String cedula, String motivo, java.util.Map<String, String> context)
    {
        return _iceI_AgregarSospechosoAsync(cedula, motivo, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> AgregarSospechosoAsync(String cedula, String motivo)
    {
        return _iceI_AgregarSospechosoAsync(cedula, motivo, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> AgregarSospechosoAsync(String cedula, String motivo, java.util.Map<String, String> context)
    {
        return _iceI_AgregarSospechosoAsync(cedula, motivo, context, false);
    }

    /**
     * @hidden
     * @param iceP_cedula -
     * @param iceP_motivo -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_AgregarSospechosoAsync(String iceP_cedula, String iceP_motivo, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "AgregarSospechoso", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_cedula);
                     ostr.writeString(iceP_motivo);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    default boolean RegistrarLogs(LogEntry log)
    {
        return RegistrarLogs(log, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean RegistrarLogs(LogEntry log, java.util.Map<String, String> context)
    {
        return _iceI_RegistrarLogsAsync(log, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> RegistrarLogsAsync(LogEntry log)
    {
        return _iceI_RegistrarLogsAsync(log, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> RegistrarLogsAsync(LogEntry log, java.util.Map<String, String> context)
    {
        return _iceI_RegistrarLogsAsync(log, context, false);
    }

    /**
     * @hidden
     * @param iceP_log -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_RegistrarLogsAsync(LogEntry iceP_log, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "RegistrarLogs", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     LogEntry.ice_write(ostr, iceP_log);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    default int GetConteoVotosPorCandidato(int candidatoId)
    {
        return GetConteoVotosPorCandidato(candidatoId, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default int GetConteoVotosPorCandidato(int candidatoId, java.util.Map<String, String> context)
    {
        return _iceI_GetConteoVotosPorCandidatoAsync(candidatoId, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Integer> GetConteoVotosPorCandidatoAsync(int candidatoId)
    {
        return _iceI_GetConteoVotosPorCandidatoAsync(candidatoId, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Integer> GetConteoVotosPorCandidatoAsync(int candidatoId, java.util.Map<String, String> context)
    {
        return _iceI_GetConteoVotosPorCandidatoAsync(candidatoId, context, false);
    }

    /**
     * @hidden
     * @param iceP_candidatoId -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Integer> _iceI_GetConteoVotosPorCandidatoAsync(int iceP_candidatoId, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Integer> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "GetConteoVotosPorCandidato", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeInt(iceP_candidatoId);
                 }, istr -> {
                     int ret;
                     ret = istr.readInt();
                     return ret;
                 });
        return f;
    }

    default String ConsultarMesaDescriptiva(String cedula)
    {
        return ConsultarMesaDescriptiva(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default String ConsultarMesaDescriptiva(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_ConsultarMesaDescriptivaAsync(cedula, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.String> ConsultarMesaDescriptivaAsync(String cedula)
    {
        return _iceI_ConsultarMesaDescriptivaAsync(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.String> ConsultarMesaDescriptivaAsync(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_ConsultarMesaDescriptivaAsync(cedula, context, false);
    }

    /**
     * @hidden
     * @param iceP_cedula -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.String> _iceI_ConsultarMesaDescriptivaAsync(String iceP_cedula, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.String> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "ConsultarMesaDescriptiva", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_cedula);
                 }, istr -> {
                     String ret;
                     ret = istr.readString();
                     return ret;
                 });
        return f;
    }

    default boolean YaVoto(String cedula)
    {
        return YaVoto(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean YaVoto(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_YaVotoAsync(cedula, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> YaVotoAsync(String cedula)
    {
        return _iceI_YaVotoAsync(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> YaVotoAsync(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_YaVotoAsync(cedula, context, false);
    }

    /**
     * @hidden
     * @param iceP_cedula -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_YaVotoAsync(String iceP_cedula, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "YaVoto", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_cedula);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    default boolean EsSospechoso(String cedula)
    {
        return EsSospechoso(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean EsSospechoso(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_EsSospechosoAsync(cedula, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> EsSospechosoAsync(String cedula)
    {
        return _iceI_EsSospechosoAsync(cedula, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> EsSospechosoAsync(String cedula, java.util.Map<String, String> context)
    {
        return _iceI_EsSospechosoAsync(cedula, context, false);
    }

    /**
     * @hidden
     * @param iceP_cedula -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_EsSospechosoAsync(String iceP_cedula, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "EsSospechoso", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_cedula);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static ProxyCacheDBCiudadPrx checkedCast(com.zeroc.Ice.ObjectPrx obj)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, ice_staticId(), ProxyCacheDBCiudadPrx.class, _ProxyCacheDBCiudadPrxI.class);
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param context The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static ProxyCacheDBCiudadPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, java.util.Map<String, String> context)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, context, ice_staticId(), ProxyCacheDBCiudadPrx.class, _ProxyCacheDBCiudadPrxI.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static ProxyCacheDBCiudadPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, String facet)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, facet, ice_staticId(), ProxyCacheDBCiudadPrx.class, _ProxyCacheDBCiudadPrxI.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @param context The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static ProxyCacheDBCiudadPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, String facet, java.util.Map<String, String> context)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, facet, context, ice_staticId(), ProxyCacheDBCiudadPrx.class, _ProxyCacheDBCiudadPrxI.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param obj The untyped proxy.
     * @return A proxy for this type.
     **/
    static ProxyCacheDBCiudadPrx uncheckedCast(com.zeroc.Ice.ObjectPrx obj)
    {
        return com.zeroc.Ice.ObjectPrx._uncheckedCast(obj, ProxyCacheDBCiudadPrx.class, _ProxyCacheDBCiudadPrxI.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @return A proxy for this type.
     **/
    static ProxyCacheDBCiudadPrx uncheckedCast(com.zeroc.Ice.ObjectPrx obj, String facet)
    {
        return com.zeroc.Ice.ObjectPrx._uncheckedCast(obj, facet, ProxyCacheDBCiudadPrx.class, _ProxyCacheDBCiudadPrxI.class);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the per-proxy context.
     * @param newContext The context for the new proxy.
     * @return A proxy with the specified per-proxy context.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_context(java.util.Map<String, String> newContext)
    {
        return (ProxyCacheDBCiudadPrx)_ice_context(newContext);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the adapter ID.
     * @param newAdapterId The adapter ID for the new proxy.
     * @return A proxy with the specified adapter ID.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_adapterId(String newAdapterId)
    {
        return (ProxyCacheDBCiudadPrx)_ice_adapterId(newAdapterId);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the endpoints.
     * @param newEndpoints The endpoints for the new proxy.
     * @return A proxy with the specified endpoints.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_endpoints(com.zeroc.Ice.Endpoint[] newEndpoints)
    {
        return (ProxyCacheDBCiudadPrx)_ice_endpoints(newEndpoints);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the locator cache timeout.
     * @param newTimeout The new locator cache timeout (in seconds).
     * @return A proxy with the specified locator cache timeout.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_locatorCacheTimeout(int newTimeout)
    {
        return (ProxyCacheDBCiudadPrx)_ice_locatorCacheTimeout(newTimeout);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the invocation timeout.
     * @param newTimeout The new invocation timeout (in seconds).
     * @return A proxy with the specified invocation timeout.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_invocationTimeout(int newTimeout)
    {
        return (ProxyCacheDBCiudadPrx)_ice_invocationTimeout(newTimeout);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for connection caching.
     * @param newCache <code>true</code> if the new proxy should cache connections; <code>false</code> otherwise.
     * @return A proxy with the specified caching policy.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_connectionCached(boolean newCache)
    {
        return (ProxyCacheDBCiudadPrx)_ice_connectionCached(newCache);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the endpoint selection policy.
     * @param newType The new endpoint selection policy.
     * @return A proxy with the specified endpoint selection policy.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_endpointSelection(com.zeroc.Ice.EndpointSelectionType newType)
    {
        return (ProxyCacheDBCiudadPrx)_ice_endpointSelection(newType);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for how it selects endpoints.
     * @param b If <code>b</code> is <code>true</code>, only endpoints that use a secure transport are
     * used by the new proxy. If <code>b</code> is false, the returned proxy uses both secure and
     * insecure endpoints.
     * @return A proxy with the specified selection policy.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_secure(boolean b)
    {
        return (ProxyCacheDBCiudadPrx)_ice_secure(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the encoding used to marshal parameters.
     * @param e The encoding version to use to marshal request parameters.
     * @return A proxy with the specified encoding version.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_encodingVersion(com.zeroc.Ice.EncodingVersion e)
    {
        return (ProxyCacheDBCiudadPrx)_ice_encodingVersion(e);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its endpoint selection policy.
     * @param b If <code>b</code> is <code>true</code>, the new proxy will use secure endpoints for invocations
     * and only use insecure endpoints if an invocation cannot be made via secure endpoints. If <code>b</code> is
     * <code>false</code>, the proxy prefers insecure endpoints to secure ones.
     * @return A proxy with the specified selection policy.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_preferSecure(boolean b)
    {
        return (ProxyCacheDBCiudadPrx)_ice_preferSecure(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the router.
     * @param router The router for the new proxy.
     * @return A proxy with the specified router.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_router(com.zeroc.Ice.RouterPrx router)
    {
        return (ProxyCacheDBCiudadPrx)_ice_router(router);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the locator.
     * @param locator The locator for the new proxy.
     * @return A proxy with the specified locator.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_locator(com.zeroc.Ice.LocatorPrx locator)
    {
        return (ProxyCacheDBCiudadPrx)_ice_locator(locator);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for collocation optimization.
     * @param b <code>true</code> if the new proxy enables collocation optimization; <code>false</code> otherwise.
     * @return A proxy with the specified collocation optimization.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_collocationOptimized(boolean b)
    {
        return (ProxyCacheDBCiudadPrx)_ice_collocationOptimized(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses twoway invocations.
     * @return A proxy that uses twoway invocations.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_twoway()
    {
        return (ProxyCacheDBCiudadPrx)_ice_twoway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses oneway invocations.
     * @return A proxy that uses oneway invocations.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_oneway()
    {
        return (ProxyCacheDBCiudadPrx)_ice_oneway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses batch oneway invocations.
     * @return A proxy that uses batch oneway invocations.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_batchOneway()
    {
        return (ProxyCacheDBCiudadPrx)_ice_batchOneway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses datagram invocations.
     * @return A proxy that uses datagram invocations.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_datagram()
    {
        return (ProxyCacheDBCiudadPrx)_ice_datagram();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses batch datagram invocations.
     * @return A proxy that uses batch datagram invocations.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_batchDatagram()
    {
        return (ProxyCacheDBCiudadPrx)_ice_batchDatagram();
    }

    /**
     * Returns a proxy that is identical to this proxy, except for compression.
     * @param co <code>true</code> enables compression for the new proxy; <code>false</code> disables compression.
     * @return A proxy with the specified compression setting.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_compress(boolean co)
    {
        return (ProxyCacheDBCiudadPrx)_ice_compress(co);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its connection timeout setting.
     * @param t The connection timeout for the proxy in milliseconds.
     * @return A proxy with the specified timeout.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_timeout(int t)
    {
        return (ProxyCacheDBCiudadPrx)_ice_timeout(t);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its connection ID.
     * @param connectionId The connection ID for the new proxy. An empty string removes the connection ID.
     * @return A proxy with the specified connection ID.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_connectionId(String connectionId)
    {
        return (ProxyCacheDBCiudadPrx)_ice_connectionId(connectionId);
    }

    /**
     * Returns a proxy that is identical to this proxy, except it's a fixed proxy bound
     * the given connection.@param connection The fixed proxy connection.
     * @return A fixed proxy bound to the given connection.
     **/
    @Override
    default ProxyCacheDBCiudadPrx ice_fixed(com.zeroc.Ice.Connection connection)
    {
        return (ProxyCacheDBCiudadPrx)_ice_fixed(connection);
    }

    static String ice_staticId()
    {
        return "::VotingSystem::ProxyCacheDBCiudad";
    }
}
