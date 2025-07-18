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

public interface MainServerPrx extends com.zeroc.Ice.ObjectPrx
{
    default MesaInfo[] listarMesas()
    {
        return listarMesas(com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default MesaInfo[] listarMesas(java.util.Map<String, String> context)
    {
        return _iceI_listarMesasAsync(context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<MesaInfo[]> listarMesasAsync()
    {
        return _iceI_listarMesasAsync(com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<MesaInfo[]> listarMesasAsync(java.util.Map<String, String> context)
    {
        return _iceI_listarMesasAsync(context, false);
    }

    /**
     * @hidden
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<MesaInfo[]> _iceI_listarMesasAsync(java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<MesaInfo[]> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "listarMesas", null, sync, null);
        f.invoke(true, context, null, null, istr -> {
                     MesaInfo[] ret;
                     ret = MesaInfoSeqHelper.read(istr);
                     return ret;
                 });
        return f;
    }

    default boolean validarVoto(String idVotante)
    {
        return validarVoto(idVotante, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean validarVoto(String idVotante, java.util.Map<String, String> context)
    {
        return _iceI_validarVotoAsync(idVotante, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> validarVotoAsync(String idVotante)
    {
        return _iceI_validarVotoAsync(idVotante, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> validarVotoAsync(String idVotante, java.util.Map<String, String> context)
    {
        return _iceI_validarVotoAsync(idVotante, context, false);
    }

    /**
     * @hidden
     * @param iceP_idVotante -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_validarVotoAsync(String iceP_idVotante, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "validarVoto", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_idVotante);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    default boolean registrarVoto(String idVotante, int idCandidato)
    {
        return registrarVoto(idVotante, idCandidato, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean registrarVoto(String idVotante, int idCandidato, java.util.Map<String, String> context)
    {
        return _iceI_registrarVotoAsync(idVotante, idCandidato, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> registrarVotoAsync(String idVotante, int idCandidato)
    {
        return _iceI_registrarVotoAsync(idVotante, idCandidato, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> registrarVotoAsync(String idVotante, int idCandidato, java.util.Map<String, String> context)
    {
        return _iceI_registrarVotoAsync(idVotante, idCandidato, context, false);
    }

    /**
     * @hidden
     * @param iceP_idVotante -
     * @param iceP_idCandidato -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_registrarVotoAsync(String iceP_idVotante, int iceP_idCandidato, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "registrarVoto", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_idVotante);
                     ostr.writeInt(iceP_idCandidato);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    default boolean verificarEstado(String idVotante)
    {
        return verificarEstado(idVotante, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean verificarEstado(String idVotante, java.util.Map<String, String> context)
    {
        return _iceI_verificarEstadoAsync(idVotante, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> verificarEstadoAsync(String idVotante)
    {
        return _iceI_verificarEstadoAsync(idVotante, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> verificarEstadoAsync(String idVotante, java.util.Map<String, String> context)
    {
        return _iceI_verificarEstadoAsync(idVotante, context, false);
    }

    /**
     * @hidden
     * @param iceP_idVotante -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_verificarEstadoAsync(String iceP_idVotante, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "verificarEstado", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_idVotante);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    default boolean addLoteVotos(LoteVotos lote)
    {
        return addLoteVotos(lote, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean addLoteVotos(LoteVotos lote, java.util.Map<String, String> context)
    {
        return _iceI_addLoteVotosAsync(lote, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> addLoteVotosAsync(LoteVotos lote)
    {
        return _iceI_addLoteVotosAsync(lote, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> addLoteVotosAsync(LoteVotos lote, java.util.Map<String, String> context)
    {
        return _iceI_addLoteVotosAsync(lote, context, false);
    }

    /**
     * @hidden
     * @param iceP_lote -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_addLoteVotosAsync(LoteVotos iceP_lote, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "addLoteVotos", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     LoteVotos.ice_write(ostr, iceP_lote);
                 }, istr -> {
                     boolean ret;
                     ret = istr.readBool();
                     return ret;
                 });
        return f;
    }

    default Candidato[] listarCandidatos()
    {
        return listarCandidatos(com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default Candidato[] listarCandidatos(java.util.Map<String, String> context)
    {
        return _iceI_listarCandidatosAsync(context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<Candidato[]> listarCandidatosAsync()
    {
        return _iceI_listarCandidatosAsync(com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Candidato[]> listarCandidatosAsync(java.util.Map<String, String> context)
    {
        return _iceI_listarCandidatosAsync(context, false);
    }

    /**
     * @hidden
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Candidato[]> _iceI_listarCandidatosAsync(java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Candidato[]> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "listarCandidatos", null, sync, null);
        f.invoke(true, context, null, null, istr -> {
                     Candidato[] ret;
                     ret = CandidatoSeqHelper.read(istr);
                     return ret;
                 });
        return f;
    }

    default void registrarAlerta(AlertaInfo alerta)
    {
        registrarAlerta(alerta, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default void registrarAlerta(AlertaInfo alerta, java.util.Map<String, String> context)
    {
        _iceI_registrarAlertaAsync(alerta, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<Void> registrarAlertaAsync(AlertaInfo alerta)
    {
        return _iceI_registrarAlertaAsync(alerta, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Void> registrarAlertaAsync(AlertaInfo alerta, java.util.Map<String, String> context)
    {
        return _iceI_registrarAlertaAsync(alerta, context, false);
    }

    /**
     * @hidden
     * @param iceP_alerta -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Void> _iceI_registrarAlertaAsync(AlertaInfo iceP_alerta, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Void> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "registrarAlerta", null, sync, null);
        f.invoke(false, context, null, ostr -> {
                     AlertaInfo.ice_write(ostr, iceP_alerta);
                 }, null);
        return f;
    }

    default AlertaInfo[] listarAlertas()
    {
        return listarAlertas(com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default AlertaInfo[] listarAlertas(java.util.Map<String, String> context)
    {
        return _iceI_listarAlertasAsync(context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<AlertaInfo[]> listarAlertasAsync()
    {
        return _iceI_listarAlertasAsync(com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<AlertaInfo[]> listarAlertasAsync(java.util.Map<String, String> context)
    {
        return _iceI_listarAlertasAsync(context, false);
    }

    /**
     * @hidden
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<AlertaInfo[]> _iceI_listarAlertasAsync(java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<AlertaInfo[]> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "listarAlertas", null, sync, null);
        f.invoke(true, context, null, null, istr -> {
                     AlertaInfo[] ret;
                     ret = AlertaInfoSeqHelper.read(istr);
                     return ret;
                 });
        return f;
    }

    default Estadisticas obtenerEstadisticas()
    {
        return obtenerEstadisticas(com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default Estadisticas obtenerEstadisticas(java.util.Map<String, String> context)
    {
        return _iceI_obtenerEstadisticasAsync(context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<Estadisticas> obtenerEstadisticasAsync()
    {
        return _iceI_obtenerEstadisticasAsync(com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Estadisticas> obtenerEstadisticasAsync(java.util.Map<String, String> context)
    {
        return _iceI_obtenerEstadisticasAsync(context, false);
    }

    /**
     * @hidden
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Estadisticas> _iceI_obtenerEstadisticasAsync(java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Estadisticas> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "obtenerEstadisticas", null, sync, null);
        f.invoke(true, context, null, null, istr -> {
                     Estadisticas ret;
                     ret = Estadisticas.ice_read(istr);
                     return ret;
                 });
        return f;
    }

    default boolean verificarEstadoZona(String idVotante, String zona)
    {
        return verificarEstadoZona(idVotante, zona, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default boolean verificarEstadoZona(String idVotante, String zona, java.util.Map<String, String> context)
    {
        return _iceI_verificarEstadoZonaAsync(idVotante, zona, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> verificarEstadoZonaAsync(String idVotante, String zona)
    {
        return _iceI_verificarEstadoZonaAsync(idVotante, zona, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<java.lang.Boolean> verificarEstadoZonaAsync(String idVotante, String zona, java.util.Map<String, String> context)
    {
        return _iceI_verificarEstadoZonaAsync(idVotante, zona, context, false);
    }

    /**
     * @hidden
     * @param iceP_idVotante -
     * @param iceP_zona -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> _iceI_verificarEstadoZonaAsync(String iceP_idVotante, String iceP_zona, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<java.lang.Boolean> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "verificarEstadoZona", null, sync, null);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeString(iceP_idVotante);
                     ostr.writeString(iceP_zona);
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
    static MainServerPrx checkedCast(com.zeroc.Ice.ObjectPrx obj)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, ice_staticId(), MainServerPrx.class, _MainServerPrxI.class);
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param context The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static MainServerPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, java.util.Map<String, String> context)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, context, ice_staticId(), MainServerPrx.class, _MainServerPrxI.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static MainServerPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, String facet)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, facet, ice_staticId(), MainServerPrx.class, _MainServerPrxI.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @param context The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static MainServerPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, String facet, java.util.Map<String, String> context)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, facet, context, ice_staticId(), MainServerPrx.class, _MainServerPrxI.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param obj The untyped proxy.
     * @return A proxy for this type.
     **/
    static MainServerPrx uncheckedCast(com.zeroc.Ice.ObjectPrx obj)
    {
        return com.zeroc.Ice.ObjectPrx._uncheckedCast(obj, MainServerPrx.class, _MainServerPrxI.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @return A proxy for this type.
     **/
    static MainServerPrx uncheckedCast(com.zeroc.Ice.ObjectPrx obj, String facet)
    {
        return com.zeroc.Ice.ObjectPrx._uncheckedCast(obj, facet, MainServerPrx.class, _MainServerPrxI.class);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the per-proxy context.
     * @param newContext The context for the new proxy.
     * @return A proxy with the specified per-proxy context.
     **/
    @Override
    default MainServerPrx ice_context(java.util.Map<String, String> newContext)
    {
        return (MainServerPrx)_ice_context(newContext);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the adapter ID.
     * @param newAdapterId The adapter ID for the new proxy.
     * @return A proxy with the specified adapter ID.
     **/
    @Override
    default MainServerPrx ice_adapterId(String newAdapterId)
    {
        return (MainServerPrx)_ice_adapterId(newAdapterId);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the endpoints.
     * @param newEndpoints The endpoints for the new proxy.
     * @return A proxy with the specified endpoints.
     **/
    @Override
    default MainServerPrx ice_endpoints(com.zeroc.Ice.Endpoint[] newEndpoints)
    {
        return (MainServerPrx)_ice_endpoints(newEndpoints);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the locator cache timeout.
     * @param newTimeout The new locator cache timeout (in seconds).
     * @return A proxy with the specified locator cache timeout.
     **/
    @Override
    default MainServerPrx ice_locatorCacheTimeout(int newTimeout)
    {
        return (MainServerPrx)_ice_locatorCacheTimeout(newTimeout);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the invocation timeout.
     * @param newTimeout The new invocation timeout (in seconds).
     * @return A proxy with the specified invocation timeout.
     **/
    @Override
    default MainServerPrx ice_invocationTimeout(int newTimeout)
    {
        return (MainServerPrx)_ice_invocationTimeout(newTimeout);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for connection caching.
     * @param newCache <code>true</code> if the new proxy should cache connections; <code>false</code> otherwise.
     * @return A proxy with the specified caching policy.
     **/
    @Override
    default MainServerPrx ice_connectionCached(boolean newCache)
    {
        return (MainServerPrx)_ice_connectionCached(newCache);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the endpoint selection policy.
     * @param newType The new endpoint selection policy.
     * @return A proxy with the specified endpoint selection policy.
     **/
    @Override
    default MainServerPrx ice_endpointSelection(com.zeroc.Ice.EndpointSelectionType newType)
    {
        return (MainServerPrx)_ice_endpointSelection(newType);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for how it selects endpoints.
     * @param b If <code>b</code> is <code>true</code>, only endpoints that use a secure transport are
     * used by the new proxy. If <code>b</code> is false, the returned proxy uses both secure and
     * insecure endpoints.
     * @return A proxy with the specified selection policy.
     **/
    @Override
    default MainServerPrx ice_secure(boolean b)
    {
        return (MainServerPrx)_ice_secure(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the encoding used to marshal parameters.
     * @param e The encoding version to use to marshal request parameters.
     * @return A proxy with the specified encoding version.
     **/
    @Override
    default MainServerPrx ice_encodingVersion(com.zeroc.Ice.EncodingVersion e)
    {
        return (MainServerPrx)_ice_encodingVersion(e);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its endpoint selection policy.
     * @param b If <code>b</code> is <code>true</code>, the new proxy will use secure endpoints for invocations
     * and only use insecure endpoints if an invocation cannot be made via secure endpoints. If <code>b</code> is
     * <code>false</code>, the proxy prefers insecure endpoints to secure ones.
     * @return A proxy with the specified selection policy.
     **/
    @Override
    default MainServerPrx ice_preferSecure(boolean b)
    {
        return (MainServerPrx)_ice_preferSecure(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the router.
     * @param router The router for the new proxy.
     * @return A proxy with the specified router.
     **/
    @Override
    default MainServerPrx ice_router(com.zeroc.Ice.RouterPrx router)
    {
        return (MainServerPrx)_ice_router(router);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the locator.
     * @param locator The locator for the new proxy.
     * @return A proxy with the specified locator.
     **/
    @Override
    default MainServerPrx ice_locator(com.zeroc.Ice.LocatorPrx locator)
    {
        return (MainServerPrx)_ice_locator(locator);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for collocation optimization.
     * @param b <code>true</code> if the new proxy enables collocation optimization; <code>false</code> otherwise.
     * @return A proxy with the specified collocation optimization.
     **/
    @Override
    default MainServerPrx ice_collocationOptimized(boolean b)
    {
        return (MainServerPrx)_ice_collocationOptimized(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses twoway invocations.
     * @return A proxy that uses twoway invocations.
     **/
    @Override
    default MainServerPrx ice_twoway()
    {
        return (MainServerPrx)_ice_twoway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses oneway invocations.
     * @return A proxy that uses oneway invocations.
     **/
    @Override
    default MainServerPrx ice_oneway()
    {
        return (MainServerPrx)_ice_oneway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses batch oneway invocations.
     * @return A proxy that uses batch oneway invocations.
     **/
    @Override
    default MainServerPrx ice_batchOneway()
    {
        return (MainServerPrx)_ice_batchOneway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses datagram invocations.
     * @return A proxy that uses datagram invocations.
     **/
    @Override
    default MainServerPrx ice_datagram()
    {
        return (MainServerPrx)_ice_datagram();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses batch datagram invocations.
     * @return A proxy that uses batch datagram invocations.
     **/
    @Override
    default MainServerPrx ice_batchDatagram()
    {
        return (MainServerPrx)_ice_batchDatagram();
    }

    /**
     * Returns a proxy that is identical to this proxy, except for compression.
     * @param co <code>true</code> enables compression for the new proxy; <code>false</code> disables compression.
     * @return A proxy with the specified compression setting.
     **/
    @Override
    default MainServerPrx ice_compress(boolean co)
    {
        return (MainServerPrx)_ice_compress(co);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its connection timeout setting.
     * @param t The connection timeout for the proxy in milliseconds.
     * @return A proxy with the specified timeout.
     **/
    @Override
    default MainServerPrx ice_timeout(int t)
    {
        return (MainServerPrx)_ice_timeout(t);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its connection ID.
     * @param connectionId The connection ID for the new proxy. An empty string removes the connection ID.
     * @return A proxy with the specified connection ID.
     **/
    @Override
    default MainServerPrx ice_connectionId(String connectionId)
    {
        return (MainServerPrx)_ice_connectionId(connectionId);
    }

    /**
     * Returns a proxy that is identical to this proxy, except it's a fixed proxy bound
     * the given connection.@param connection The fixed proxy connection.
     * @return A fixed proxy bound to the given connection.
     **/
    @Override
    default MainServerPrx ice_fixed(com.zeroc.Ice.Connection connection)
    {
        return (MainServerPrx)_ice_fixed(connection);
    }

    static String ice_staticId()
    {
        return "::VotingSystem::MainServer";
    }
}
